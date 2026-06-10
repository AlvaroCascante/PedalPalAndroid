package com.quetoquenana.and.features.profile.data.repository

import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.domain.repository.MediaRepository
import com.quetoquenana.and.features.authentication.data.local.datasource.SessionLocalDataSource
import com.quetoquenana.and.features.profile.data.local.datasource.ProfileLocalDataSource
import com.quetoquenana.and.features.profile.data.local.entity.toDomain
import com.quetoquenana.and.features.profile.data.local.entity.toEntity
import com.quetoquenana.and.features.profile.data.remote.dataSource.ProfileRemoteDataSource
import com.quetoquenana.and.features.profile.data.remote.dto.toDomain
import com.quetoquenana.and.features.profile.domain.model.Profile
import com.quetoquenana.and.features.profile.domain.repository.ProfileRepository
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val localDataSource: ProfileLocalDataSource,
    private val remote: ProfileRemoteDataSource,
    private val mediaRepository: MediaRepository,
) : ProfileRepository {

    override suspend fun getCurrentUserProfile(): Profile {
        // 1) get active session once (suspend)
        val session = sessionLocalDataSource.getSession() ?: throw IllegalStateException("No active session found")

        return try {
            getFromLocalOrThrow(userId = session.userId)
        } catch (t: Throwable) {
            Timber.w(t = t, message = "Failed to load profile from local for ${session.userId}")
            refreshProfile(session.userId)
            return getFromLocalOrThrow(userId = session.userId)
        }
    }

    private suspend fun getFromLocalOrThrow(userId: UUID): Profile {
        val localProfile = localDataSource.getCurrentProfile(userId = userId)

        if (localProfile.isSuccess) {
            // Local profile present: try to fetch single media (one-shot) and combine
            val profileEntity = localProfile.getOrThrow()
            val media = try {
                mediaRepository.getSingleMedia(
                    referenceId = userId,
                    referenceType = MediaReferenceType.PROFILE
                )
            } catch (t: Throwable) {
                Timber.w(t = t, message = "Failed to load profile media for $userId")
                null
            }
            return profileEntity.toDomain(profileImageUrl = media?.url)
        } else {
            throw IllegalStateException("No local profile found for userId=$userId")
        }
    }
    private suspend fun refreshProfile(userId: UUID) {
        try {
            val remote = remote.getProfile(userId).toDomain()
            localDataSource.saveProfile(remote.toEntity())
        } catch (e : Exception) {
            Timber.e(t = e, message = "Failed to refresh user profile for userId: $userId")
        }
    }

    override suspend fun uploadProfilePhoto(request: MediaUploadRequest) {
        mediaRepository.replaceMedia(
            referenceId = request.referenceId,
            referenceType = MediaReferenceType.PROFILE,
            media = request
        )
    }
}

