package com.quetoquenana.and.core.media.data.remote.dataSource

import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.data.remote.api.MediaApi
import com.quetoquenana.and.core.media.domain.model.toCreateMediaRequestDto
import javax.inject.Inject

class MediaRemoteDataSourceRetrofit @Inject constructor(
    private val mediaApi: MediaApi,
) : MediaRemoteDataSource {

    override suspend fun getMedia(referenceId: String, referenceType: MediaReferenceType): List<MediaFileResponseDto> {
        return mediaApi.getMedia(
            referenceType = referenceType.mediaName,
            id = referenceId,
        ).data
    }

    override suspend fun createMedia(
        referenceId: String,
        referenceType: MediaReferenceType,
        uploads: List<MediaUploadRequest>,
    ): List<MediaFileResponseDto> {
        return mediaApi.createMedia(
            id = referenceId,
            request = uploads.toCreateMediaRequestDto(referenceType = referenceType.mediaName),
        ).data
    }

    override suspend fun confirmMedia(mediaId: String) {
        val response = mediaApi.confirmMedia(id = mediaId)
        if (!response.isSuccessful) {
            throw IllegalStateException("Unable to confirm uploaded media")
        }
    }
}

