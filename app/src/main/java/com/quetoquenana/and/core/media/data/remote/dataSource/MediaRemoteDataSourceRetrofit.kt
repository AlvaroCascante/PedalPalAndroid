package com.quetoquenana.and.core.media.data.remote.dataSource

import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.data.remote.api.MediaApi
import com.quetoquenana.and.core.media.domain.model.toCreateMediaRequestDto
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class MediaRemoteDataSourceRetrofit @Inject constructor(
    private val mediaApi: MediaApi,
) : MediaRemoteDataSource {

    override suspend fun getMedia(referenceId: UUID, referenceType: MediaReferenceType): List<MediaFileResponseDto> {
        val response = mediaApi.getMedia(
            referenceType = referenceType.mediaName,
            id = referenceId,
        )
        return response.data
    }

    override suspend fun createMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType,
        uploads: List<MediaUploadRequest>,
    ): List<MediaFileResponseDto> {
        Timber.d(
            "Creating media records. referenceType=%s referenceId=%s uploads=%s correlationIds=%s",
            referenceType.mediaName,
            referenceId,
            uploads.size,
            uploads.joinToString(prefix = "[", postfix = "]") { it.correlationId.toString() },
        )
        val result = mediaApi.createMedia(
            id = referenceId,
            request = uploads.toCreateMediaRequestDto(referenceType = referenceType.mediaName),
        )
        Timber.d(
            "Created media records. referenceType=%s referenceId=%s count=%s remoteIds=%s",
            referenceType.mediaName,
            referenceId,
            result.data.size,
            result.data.joinToString(prefix = "[", postfix = "]") { it.id.toString() },
        )
        return result.data
    }

    override suspend fun confirmMedia(mediaId: UUID): Result<MediaFileResponseDto> {
        return runCatching {
            Timber.d("Confirming media upload. mediaId=%s", mediaId)
            val response = mediaApi.confirmMedia(id = mediaId)
            response.data
        }.onFailure { throwable ->
            Timber.e(throwable, "Failed to confirm media upload. mediaId=%s", mediaId)
        }
    }
}

