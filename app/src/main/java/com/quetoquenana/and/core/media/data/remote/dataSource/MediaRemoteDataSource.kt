package com.quetoquenana.and.core.media.data.remote.dataSource

import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest

interface MediaRemoteDataSource {
    suspend fun getMedia(referenceId: String, referenceType: MediaReferenceType): List<MediaFileResponseDto>
    suspend fun createMedia(
        referenceId: String,
        referenceType: MediaReferenceType,
        uploads: List<MediaUploadRequest>,
    ): List<MediaFileResponseDto>
    suspend fun confirmMedia(mediaId: String)
}

