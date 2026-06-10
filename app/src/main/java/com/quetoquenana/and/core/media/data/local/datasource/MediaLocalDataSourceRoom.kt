package com.quetoquenana.and.core.media.data.local.datasource

import com.quetoquenana.and.core.media.data.local.dao.MediaDao
import com.quetoquenana.and.core.media.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class MediaLocalDataSourceRoom @Inject constructor(
    private val mediaDao: MediaDao,
) : MediaLocalDataSource {

    override fun observeMedia(referenceId: UUID, referenceType: String): Flow<List<MediaEntity>> {
        return mediaDao.observeByReference(referenceId = referenceId, referenceType = referenceType)
    }

    override suspend fun getMedia(referenceId: UUID, referenceType: String): List<MediaEntity> {
        return mediaDao.getByReference(referenceId = referenceId, referenceType = referenceType)
    }

    override suspend fun getSingleMedia(
        referenceId: UUID,
        referenceType: String
    ): MediaEntity? {
        return mediaDao.getSingleByReference(referenceId = referenceId, referenceType = referenceType)
    }

    override suspend fun saveAllMedia(media: List<MediaEntity>) {
        mediaDao.upsertAll(media)
    }

    override suspend fun updateMedia(media: MediaEntity) {
        mediaDao.upsert(media)
    }

    override suspend fun clearMedia() {
        mediaDao.deleteAll()
    }
}

