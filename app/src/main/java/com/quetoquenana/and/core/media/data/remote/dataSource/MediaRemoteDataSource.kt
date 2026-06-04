package com.quetoquenana.and.core.media.data.remote.dataSource

import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import java.util.UUID

interface MediaRemoteDataSource {
    suspend fun getMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType
    ): List<MediaFileResponseDto>

    suspend fun createMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType,
        uploads: List<MediaUploadRequest>,
    ): List<MediaFileResponseDto>

    suspend fun confirmMedia(mediaId: UUID): Result<MediaFileResponseDto>
}

