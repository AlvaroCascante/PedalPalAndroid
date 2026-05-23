package com.quetoquenana.and.core.media.data.local.datasource

import com.quetoquenana.and.core.media.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

interface MediaLocalDataSource {
    fun observeMedia(referenceId: String, referenceType: String, ): Flow<List<MediaEntity>>
    suspend fun saveAllMedia(media: List<MediaEntity>)
    suspend fun clearMedia()
}

