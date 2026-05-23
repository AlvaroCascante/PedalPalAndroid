package com.quetoquenana.and.core.media.data.local.datasource

import com.quetoquenana.and.core.media.data.local.dao.MediaDao
import com.quetoquenana.and.core.media.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaLocalDataSourceRoom @Inject constructor(
    private val mediaDao: MediaDao,
) : MediaLocalDataSource {

    override fun observeMedia(referenceId: String, referenceType: String): Flow<List<MediaEntity>> {
        return mediaDao.observeByReference(referenceId = referenceId, referenceType = referenceType)
    }

    override suspend fun saveAllMedia(media: List<MediaEntity>) {
        mediaDao.upsertAll(media)
    }

    override suspend fun clearMedia() {
        mediaDao.deleteAll()
    }
}

