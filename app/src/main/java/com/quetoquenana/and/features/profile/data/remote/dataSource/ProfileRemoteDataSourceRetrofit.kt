package com.quetoquenana.and.features.profile.data.remote.dataSource

import com.quetoquenana.and.core.media.MediaReferenceType
import com.quetoquenana.and.features.profile.data.remote.api.ProfileApi
import com.quetoquenana.and.features.profile.data.remote.api.ProfileMediaApi
import com.quetoquenana.and.features.profile.data.remote.dto.ProfileMediaFileResponseDto
import com.quetoquenana.and.features.profile.data.remote.dto.ProfileResponseDto
import com.quetoquenana.and.features.profile.data.remote.dto.toCreateProfileMediaRequestDto
import com.quetoquenana.and.features.profile.domain.model.ProfilePhotoUploadRequest
import javax.inject.Inject

class ProfileRemoteDataSourceRetrofit @Inject constructor(
    private val profileApi: ProfileApi,
    private val profileMediaApi: ProfileMediaApi,
) : ProfileRemoteDataSource {

    override suspend fun getProfile(userId: String): ProfileResponseDto {
        return profileApi.getProfile(id = userId).data
    }


    override suspend fun getMedia(
        userId: String,
        referenceType: MediaReferenceType,
    ): List<ProfileMediaFileResponseDto> {
        return profileMediaApi.getMedia(
            referenceType = referenceType.apiValue,
            id = userId,
        ).data
    }

    override suspend fun createMedia(
        userId: String,
        referenceType: MediaReferenceType,
        request: ProfilePhotoUploadRequest,
    ): List<ProfileMediaFileResponseDto> {
        return profileMediaApi.createMedia(
            id = userId,
            request = request.toCreateProfileMediaRequestDto(referenceType = referenceType),
        ).data
    }

    override suspend fun confirmMedia(mediaId: String) {
        val response = profileMediaApi.confirmMedia(id = mediaId)
        if (!response.isSuccessful) {
            throw IllegalStateException("Unable to confirm uploaded media")
        }
    }
}


