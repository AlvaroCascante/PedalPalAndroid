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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class ProfileRepositoryImplTest {

    @Test
    fun `getCurrentUserProfile combines cached profile and primary profile image`() = runTest {
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

        val result = repository.getCurrentUserProfile().first()

        assertEquals(USER_ID, result.id)
        assertEquals("Test", result.name)
        assertEquals("https://example.com/profile.png", result.profileImageUrl)
        assertEquals(USER_ID, local.observedUserId)
        assertEquals(USER_ID, mediaRepository.observedReferenceId)
        assertEquals(USER_ID, local.savedProfile?.id)
    }

    @Test
    fun `uploadProfilePhoto forwards the UUID reference id to media repository`() = runTest {
        val mediaRepository = FakeMediaRepository()
        val repository = ProfileRepositoryImpl(
            sessionLocalDataSource = FakeSessionLocalDataSource(),
            local = FakeProfileLocalDataSource(),
            remote = FakeProfileRemoteDataSource(),
            mediaRepository = mediaRepository,
        )
        val request = MediaUploadRequest(
            referenceId = USER_ID,
            name = "Profile",
            altText = "Profile image",
            contentType = "image/png",
            bytes = byteArrayOf(1, 2, 3),
            isPublic = true,
        )

        repository.uploadProfilePhoto(request)

        assertEquals(USER_ID, mediaRepository.lastUploadReferenceId)
        assertEquals(MediaReferenceType.PROFILE, mediaRepository.lastUploadReferenceType)
        assertEquals(listOf(request), mediaRepository.lastUploadRequests)
    }

    private class FakeSessionLocalDataSource : SessionLocalDataSource {
        private val session = sessionEntity()

        override suspend fun getSession(): AuthSessionEntity = session

        override fun observeActiveSession(): Flow<AuthSessionEntity?> = flowOf(session)

        override suspend fun hasActiveSession(): Boolean = true

        override suspend fun saveSession(session: AuthSessionEntity) = Unit

        override suspend fun clearSession() = Unit
    }

    private class FakeProfileLocalDataSource(
        profile: ProfileEntity = profileEntity(),
    ) : ProfileLocalDataSource {
        private val profileState = MutableStateFlow(profile)
        var observedUserId: UUID? = null
        var savedProfile: ProfileEntity? = null

        override fun observeProfile(userId: UUID): Flow<ProfileEntity> {
            observedUserId = userId
            return profileState
        }

        override suspend fun saveProfile(profile: ProfileEntity) {
            profileState.value = profile
            savedProfile = profile
        }

        override suspend fun clearProfiles() {
            profileState.value = profileEntity()
            savedProfile = null
        }
    }

    private class FakeProfileRemoteDataSource(
        private val response: ProfileResponseDto = profileResponseDto(),
    ) : ProfileRemoteDataSource {
        override suspend fun getProfile(userId: UUID): ProfileResponseDto {
            assertEquals(USER_ID, userId)
            return response
        }
    }

    private class FakeMediaRepository(
        private val media: List<MediaAsset> = emptyList(),
    ) : MediaRepository {
        var observedReferenceId: UUID? = null
        var lastUploadReferenceId: UUID? = null
        var lastUploadReferenceType: MediaReferenceType? = null
        var lastUploadRequests: List<MediaUploadRequest>? = null

        override fun observeMedia(
            referenceId: UUID,
            referenceType: MediaReferenceType,
            refresh: Boolean,
        ): Flow<List<MediaAsset>> {
            observedReferenceId = referenceId
            return flowOf(
                media.filter {
                    it.referenceId == referenceId && it.referenceType == referenceType
                }
            )
        }

        override fun observePrimaryMedia(
            referenceId: UUID,
            referenceType: MediaReferenceType,
            refresh: Boolean,
        ): Flow<MediaAsset?> {
            return observeMedia(referenceId, referenceType, refresh).map { assets -> assets.firstOrNull() }
        }

        override suspend fun refreshMedia(
            referenceId: UUID,
            referenceType: MediaReferenceType,
        ) = Unit

        override suspend fun uploadMedia(
            referenceId: UUID,
            referenceType: MediaReferenceType,
            uploads: List<MediaUploadRequest>,
        ) {
            lastUploadReferenceId = referenceId
            lastUploadReferenceType = referenceType
            lastUploadRequests = uploads
        }
    }

    private companion object {
        val USER_ID: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val MEDIA_ID: UUID = UUID.fromString("33333333-3333-3333-3333-333333333333")

        fun sessionEntity(): AuthSessionEntity {
            return AuthSessionEntity(
                userId = USER_ID,
                accessToken = "token",
                refreshToken = "refresh",
                expiresAt = null,
                isLoggedIn = true,
                lastUpdatedAt = 0L,
            )
        }

        fun profileResponseDto(): ProfileResponseDto {
            return ProfileResponseDto(
                id = USER_ID,
                person = ProfilePersonResponseDto(
                    idNumber = "123456789",
                    name = "Test",
                    lastname = "Rider",
                ),
                username = "test@example.com",
                externalId = "22222222-2222-2222-2222-222222222222",
                provider = "PASSWORD",
                nickname = "testrider",
                userStatus = "ACTIVE",
            )
        }

        fun profileEntity(): ProfileEntity {
            return ProfileEntity(
                id = USER_ID,
                name = "Cached",
                lastname = "Rider",
                idNumber = "123456789",
                username = "cached@example.com",
                nickname = "cachedrider",
            )
        }

        fun profileMedia(url: String): MediaAsset {
            return MediaAsset(
                referenceId = USER_ID,
                referenceType = MediaReferenceType.PROFILE,
                mediaId = MEDIA_ID,
                url = url,
                contentType = "IMAGE_PNG",
                name = "profile.png",
                altText = "Profile image",
                isPrivate = false,
                urlExpireAt = null,
                updatedAt = 0L,
                fetchedAt = 0L,
            )
        }
    }
}


