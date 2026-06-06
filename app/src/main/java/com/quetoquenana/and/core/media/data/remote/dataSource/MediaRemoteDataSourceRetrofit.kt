package com.quetoquenana.and.core.media.data.remote.dataSource

import com.quetoquenana.and.core.media.data.remote.api.MediaApi
import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.domain.model.toCreateMediaRequestDto
import com.quetoquenana.and.core.network.networkCall
import java.util.UUID
import javax.inject.Inject

class MediaRemoteDataSourceRetrofit @Inject constructor(
    private val mediaApi: MediaApi,
) : MediaRemoteDataSource {

    override suspend fun getMedia(referenceId: UUID, referenceType: MediaReferenceType): List<MediaFileResponseDto> {
        return networkCall {
            mediaApi.getMedia(
                referenceType = referenceType.mediaName,
                id = referenceId,
            )
        }
    }

    override suspend fun createMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType,
        uploads: List<MediaUploadRequest>,
    ): List<MediaFileResponseDto> {
        val result = networkCall {
            mediaApi.createMedia(
                id = referenceId,
                request = uploads.toCreateMediaRequestDto(referenceType = referenceType.mediaName),
            )
        }
        return result
    }

    override suspend fun confirmMedia(mediaId: UUID): MediaFileResponseDto {
        return networkCall {
            mediaApi.confirmMedia(id = mediaId)
        }
    }
}

