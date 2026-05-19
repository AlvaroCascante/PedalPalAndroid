package com.quetoquenana.and.features.profile.data.repository

import com.quetoquenana.and.core.media.MediaReferenceType
import com.quetoquenana.and.features.authentication.data.local.datasource.SessionLocalDataSource
import com.quetoquenana.and.features.profile.data.local.datasource.ProfileLocalDataSource
import com.quetoquenana.and.features.profile.data.local.entity.toDomain
import com.quetoquenana.and.features.profile.data.local.entity.toEntity
import com.quetoquenana.and.features.profile.data.remote.dataSource.ProfileMediaUploadRemoteDataSource
import com.quetoquenana.and.features.profile.data.remote.dataSource.ProfileRemoteDataSource
import com.quetoquenana.and.features.profile.data.remote.dto.primaryImage
import com.quetoquenana.and.features.profile.data.remote.dto.toDomain
import com.quetoquenana.and.features.profile.domain.model.Profile
import com.quetoquenana.and.features.profile.domain.model.ProfilePhotoUploadRequest
import com.quetoquenana.and.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import timber.log.Timber

class ProfileRepositoryImpl @Inject constructor(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val local: ProfileLocalDataSource,
    private val remote: ProfileRemoteDataSource,
    private val mediaUploadRemote: ProfileMediaUploadRemoteDataSource,
) : ProfileRepository {

    override suspend fun getProfile(): Profile {
        val userId = requireActiveUserId()
        val cachedProfile = local.getProfile(userId = userId)

        return runCatching {
            val media = remote.getMedia(
                userId = userId,
                referenceType = MediaReferenceType.PROFILE,
            ).primaryImage()
            val profile = remote.getProfile(userId = userId).toDomain(
                photoUrl = media?.url,
                profileMediaId = media?.id ?: cachedProfile?.profileMediaId,
            )
            local.saveProfile(profile.toEntity(currentTimeMillis = System.currentTimeMillis()))
            profile
        }.getOrElse { throwable ->
            Timber.w(throwable, "Unable to fetch current profile from remote")
            cachedProfile?.toDomain(photoUrl = null) ?: throw throwable
        }
    }

    override suspend fun uploadProfilePhoto(request: ProfilePhotoUploadRequest): Profile {
        val userId = requireActiveUserId()
        val cachedProfile = local.getProfile(userId = userId)
        val baseProfile = cachedProfile?.toDomain(photoUrl = null) ?: remote.getProfile(userId = userId).toDomain(
            photoUrl = null,
            profileMediaId = null,
        )
        val createdMedia = remote.createMedia(
            userId = userId,
            referenceType = MediaReferenceType.PROFILE,
            request = request,
        )
        val remoteMedia = createdMedia.primaryImage()
            ?: throw IllegalStateException("Unable to create profile image upload")

        mediaUploadRemote.uploadFile(
            url = remoteMedia.url,
            contentType = request.contentType,
            bytes = request.bytes,
        )
        remote.confirmMedia(mediaId = remoteMedia.id)

        val refreshedMedia = runCatching {
            remote.getMedia(
                userId = userId,
                referenceType = MediaReferenceType.PROFILE,
            ).primaryImage()
        }.getOrNull() ?: remoteMedia

        val profile = baseProfile.copy(
            photoUrl = refreshedMedia.url,
            profileMediaId = refreshedMedia.id,
        )
        local.saveProfile(profile.toEntity(currentTimeMillis = System.currentTimeMillis()))
        return profile
    }

    private suspend fun requireActiveUserId(): String {
        return sessionLocalDataSource.getSession()
            ?.takeIf { it.isLoggedIn }
            ?.userId
            ?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("No active session")
    }
}

