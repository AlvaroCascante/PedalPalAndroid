package com.quetoquenana.and.core.media.domain.repository

import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun observeMedia(
        referenceId: String,
        referenceType: MediaReferenceType,
        refresh: Boolean = true,
    ): Flow<List<MediaAsset>>

    fun observePrimaryMedia(
        referenceId: String,
        referenceType: MediaReferenceType,
        refresh: Boolean = true,
    ): Flow<MediaAsset?>

    suspend fun refreshMedia(
        referenceId: String,
        referenceType: MediaReferenceType,
    )

    suspend fun uploadMedia(
        referenceId: String,
        referenceType: MediaReferenceType,
        uploads: List<MediaUploadRequest>,
    )
}

