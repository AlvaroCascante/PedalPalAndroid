package com.quetoquenana.and.features.bikes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quetoquenana.and.features.bikes.data.local.entity.BikeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BikeDao {

    @Query("SELECT * FROM bikes ORDER BY name ASC")
    fun observeBikes(): Flow<List<BikeEntity>>

    @Query("SELECT * FROM bikes ORDER BY name ASC")
    suspend fun getBikes(): List<BikeEntity>

    @Query("SELECT * FROM bikes WHERE id = :id LIMIT 1")
    suspend fun getBikeById(id: String): BikeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(bike: BikeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(bikes: List<BikeEntity>)

    @Query("DELETE FROM bikes")
    suspend fun clearAll()
}
