package com.quetoquenana.and.core.media.domain.repository

import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface MediaRepository {
    fun observeMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType
    ): Flow<List<MediaAsset>>

    suspend fun getSingleMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType
    ): MediaAsset?

    suspend fun refreshMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType,
    )

    suspend fun uploadMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType,
        uploads: List<MediaUploadRequest>,
    )

    suspend fun replaceMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType,
        media: MediaUploadRequest,
    )
}

