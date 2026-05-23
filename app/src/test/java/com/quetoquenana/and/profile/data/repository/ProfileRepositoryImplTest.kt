package com.quetoquenana.and.profile.data.repository

import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.domain.repository.MediaRepository
import com.quetoquenana.and.features.authentication.data.local.datasource.SessionLocalDataSource
import com.quetoquenana.and.features.authentication.data.local.entity.AuthSessionEntity
import com.quetoquenana.and.features.profile.data.local.datasource.ProfileLocalDataSource
import com.quetoquenana.and.features.profile.data.local.entity.ProfileEntity
import com.quetoquenana.and.features.profile.data.remote.dataSource.ProfileRemoteDataSource
import com.quetoquenana.and.features.profile.data.remote.dto.ProfilePersonResponseDto
import com.quetoquenana.and.features.profile.data.remote.dto.ProfileResponseDto
import com.quetoquenana.and.features.profile.data.repository.ProfileRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class ProfileRepositoryImplTest {

    @Test
    fun `getProfile composes remote profile with local first media lookup`() = runTest {
        val mediaRepository = FakeMediaRepository(
            media = listOf(profileMedia(url = "https://example.com/profile.png"))
        )
        val local = FakeProfileLocalDataSource()
        val repository = ProfileRepositoryImpl(
            sessionLocalDataSource = FakeSessionLocalDataSource(),
            local = local,
            remote = FakeProfileRemoteDataSource(),
            mediaRepository = mediaRepository,
        )

        val result = repository.getProfile()

        assertEquals("Test", result.name)
        assertEquals("https://example.com/profile.png", result.photoUrl)
        assertEquals("media-1", result.profileMediaId)
        assertEquals(listOf(false), mediaRepository.refreshRequests)
        assertEquals("backend-user-id", local.savedProfile?.id)
    }

    @Test
    fun `getProfile returns remote profile when media lookup fails`() = runTest {
        val repository = ProfileRepositoryImpl(
            sessionLocalDataSource = FakeSessionLocalDataSource(),
            local = FakeProfileLocalDataSource(),
            remote = FakeProfileRemoteDataSource(),
            mediaRepository = FakeMediaRepository(getMediaException = IllegalStateException("media unavailable")),
        )

        val result = repository.getProfile()

        assertEquals("backend-user-id", result.id)
        assertNull(result.photoUrl)
        assertNull(result.profileMediaId)
    }

    @Test
    fun `getProfile falls back to cached profile when remote profile fetch fails`() = runTest {
        val repository = ProfileRepositoryImpl(
            sessionLocalDataSource = FakeSessionLocalDataSource(),
            local = FakeProfileLocalDataSource(
                profile = ProfileEntity(
                    id = "backend-user-id",
                    name = "Cached",
                    lastname = "Rider",
                    idNumber = "123456789",
                    username = "cached@example.com",
                    externalId = "firebase-user-id",
                    provider = "PASSWORD",
                    nickname = "cachedrider",
                    userStatus = "ACTIVE",
                    updatedAt = 0L,
                )
            ),
            remote = FakeProfileRemoteDataSource(getProfileException = IllegalStateException("offline")),
            mediaRepository = FakeMediaRepository(
                media = listOf(profileMedia(id = "cached-media", url = "https://example.com/cached.png"))
            ),
        )

        val result = repository.getProfile()

        assertEquals("Cached", result.name)
        assertEquals("https://example.com/cached.png", result.photoUrl)
        assertEquals("cached-media", result.profileMediaId)
    }

    @Test
    fun `uploadProfilePhoto stores uploaded media on the profile and saves it locally`() = runTest {
        val uploadedMedia = profileMedia(id = "media-2", url = "https://example.com/uploaded.png")
        val mediaRepository = FakeMediaRepository(uploadResult = listOf(uploadedMedia))
        val local = FakeProfileLocalDataSource()
        val repository = ProfileRepositoryImpl(
            sessionLocalDataSource = FakeSessionLocalDataSource(),
            local = local,
            remote = FakeProfileRemoteDataSource(),
            mediaRepository = mediaRepository,
        )
        val request = MediaUploadRequest(
            name = "Profile",
            altText = "Profile image",
            contentType = "image/png",
            bytes = byteArrayOf(1, 2, 3),
            isPublic = true,
        )

        val result = repository.uploadProfilePhoto(request)

        assertEquals("https://example.com/uploaded.png", result.photoUrl)
        assertEquals("media-2", result.profileMediaId)
        assertEquals("backend-user-id", mediaRepository.lastUploadReferenceId)
        assertEquals(MediaReferenceType.PROFILE, mediaRepository.lastUploadReferenceType)
        assertSame(request, mediaRepository.lastUploadRequests?.singleOrNull())
        assertEquals("backend-user-id", local.savedProfile?.id)
    }

    private class FakeSessionLocalDataSource : SessionLocalDataSource {
        override suspend fun getSession(): AuthSessionEntity {
            return AuthSessionEntity(
                userId = "backend-user-id",
                accessToken = "token",
                refreshToken = "refresh",
                expiresAt = null,
                isLoggedIn = true,
                lastUpdatedAt = 0L,
            )
        }

        override suspend fun hasActiveSession(): Boolean = true

        override suspend fun saveSession(session: AuthSessionEntity) = Unit

        override suspend fun clearSession() = Unit
    }

    private class FakeProfileLocalDataSource(
        private var profile: ProfileEntity? = null,
    ) : ProfileLocalDataSource {
        var savedProfile: ProfileEntity? = null

        override suspend fun getProfile(userId: String): ProfileEntity? = profile

        override suspend fun saveProfile(profile: ProfileEntity) {
            this.profile = profile
            savedProfile = profile
        }

        override suspend fun clearProfiles() {
            profile = null
        }
    }

    private class FakeProfileRemoteDataSource(
        private val response: ProfileResponseDto = ProfileResponseDto(
            id = "backend-user-id",
            person = ProfilePersonResponseDto(
                idNumber = "123456789",
                name = "Test",
                lastname = "Rider",
            ),
            username = "test@example.com",
            externalId = "firebase-user-id",
            provider = "PASSWORD",
            nickname = "testrider",
            userStatus = "ACTIVE",
        ),
        private val getProfileException: Throwable? = null,
    ) : ProfileRemoteDataSource {
        override suspend fun getProfile(userId: String): ProfileResponseDto {
            getProfileException?.let { throw it }
            return response
        }
    }

    private class FakeMediaRepository(
        private val media: List<MediaAsset> = emptyList(),
        private val getMediaException: Throwable? = null,
        private val uploadResult: List<MediaAsset> = media,
    ) : MediaRepository {
        val refreshRequests = mutableListOf<Boolean>()
        var lastUploadReferenceId: String? = null
        var lastUploadReferenceType: MediaReferenceType? = null
        var lastUploadRequests: List<MediaUploadRequest>? = null

        override suspend fun getMedia(
            referenceId: String,
            referenceType: MediaReferenceType,
            refresh: Boolean,
        ): List<MediaAsset> {
            refreshRequests += refresh
            getMediaException?.let { throw it }
            return media.filter {
                it.referenceId == referenceId && it.referenceType == referenceType
            }
        }

        override suspend fun uploadMedia(
            referenceId: String,
            referenceType: MediaReferenceType,
            uploads: List<MediaUploadRequest>,
        ): List<MediaAsset> {
            lastUploadReferenceId = referenceId
            lastUploadReferenceType = referenceType
            lastUploadRequests = uploads
            return uploadResult
        }
    }

    private companion object {
        fun profileMedia(
            id: String = "media-1",
            url: String,
        ): MediaAsset {
            return MediaAsset(
                referenceId = "backend-user-id",
                referenceType = MediaReferenceType.PROFILE,
                mediaId = id,
                url = url,
                contentType = "IMAGE_PNG",
                name = "profile.png",
                altText = "Profile image",
                isPrivate = false,
                updatedAt = 0L,
            )
        }
    }
}


