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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val local: ProfileLocalDataSource,
    private val remote: ProfileRemoteDataSource,
    private val mediaRepository: MediaRepository,
) : ProfileRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCurrentUserProfile(): Flow<Profile> {
        return sessionLocalDataSource.observeActiveSession()
            .filterNotNull()
            .flatMapLatest { session ->
                combine(
                    local.observeProfile(session.userId),
                    mediaRepository.observeMedia(
                        referenceId = session.userId,
                        referenceType = MediaReferenceType.PROFILE
                    )
                ) { profile, media ->
                    val url = media.firstOrNull()?.url
                    profile.toDomain(
                        profileImageUrl = url
                    )
                }
                    .onStart {

                    }
            }
    }

    private suspend fun refreshProfile(userId: UUID) {
        try {
            val remote = remote.getProfile(userId).toDomain()
            local.saveProfile(remote.toEntity())
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

