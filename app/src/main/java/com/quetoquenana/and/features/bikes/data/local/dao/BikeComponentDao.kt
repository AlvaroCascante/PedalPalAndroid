package com.quetoquenana.and.features.bikes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quetoquenana.and.features.bikes.data.local.entity.BikeComponentEntity

@Dao
interface BikeComponentDao {

    @Query("SELECT * FROM bike_components WHERE bikeId = :bikeId ORDER BY name ASC")
    suspend fun getComponentsForBike(bikeId: String): List<BikeComponentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(component: BikeComponentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(components: List<BikeComponentEntity>)

    @Query("DELETE FROM bike_components WHERE bikeId = :bikeId")
    suspend fun clearForBike(bikeId: String)

    @Query("DELETE FROM bike_components")
    suspend fun clearAll()
}
