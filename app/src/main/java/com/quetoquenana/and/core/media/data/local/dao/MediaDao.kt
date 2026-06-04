package com.quetoquenana.and.core.media.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quetoquenana.and.core.media.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MediaDao {

    @Query(value = """
        SELECT * FROM media_asset
        WHERE referenceId = :referenceId AND referenceType = :referenceType
        ORDER BY updatedAt DESC, mediaId DESC
    """)
    fun observeByReference(referenceId: UUID, referenceType: String): Flow<List<MediaEntity>>

    @Query(value = """
        SELECT * FROM media_asset
        WHERE referenceId = :referenceId AND referenceType = :referenceType
        ORDER BY updatedAt DESC, mediaId DESC
    """)
    suspend fun getByReference(referenceId: UUID, referenceType: String): List<MediaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(media: List<MediaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(media: MediaEntity)

    @Query(value = "DELETE FROM media_asset WHERE referenceId = :referenceId AND referenceType = :referenceType")
    suspend fun deleteByReference(referenceId: UUID, referenceType: String)

    @Query(value = "DELETE FROM media_asset")
    suspend fun deleteAll()
}

