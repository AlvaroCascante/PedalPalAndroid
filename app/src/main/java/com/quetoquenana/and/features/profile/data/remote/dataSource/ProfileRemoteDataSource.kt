package com.quetoquenana.and.features.profile.data.remote.dataSource

import com.quetoquenana.and.core.media.MediaReferenceType
import com.quetoquenana.and.features.profile.data.remote.dto.ProfileMediaFileResponseDto
import com.quetoquenana.and.features.profile.data.remote.dto.ProfileResponseDto
import com.quetoquenana.and.features.profile.domain.model.ProfilePhotoUploadRequest

interface ProfileRemoteDataSource {
    suspend fun getProfile(userId: String): ProfileResponseDto
    suspend fun getMedia(userId: String, referenceType: MediaReferenceType): List<ProfileMediaFileResponseDto>
    suspend fun createMedia(
        userId: String,
        referenceType: MediaReferenceType,
        request: ProfilePhotoUploadRequest,
    ): List<ProfileMediaFileResponseDto>
    suspend fun confirmMedia(mediaId: String)
}

