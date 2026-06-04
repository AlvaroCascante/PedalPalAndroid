package com.quetoquenana.and.core.media.data.local.datasource

import com.quetoquenana.and.core.media.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface MediaLocalDataSource {
    fun observeMedia(referenceId: UUID, referenceType: String): Flow<List<MediaEntity>>
    suspend fun getMedia(referenceId: UUID, referenceType: String): List<MediaEntity>
    suspend fun saveAllMedia(media: List<MediaEntity>)
    suspend fun updateMedia(media: MediaEntity)
    suspend fun clearMedia()
}

