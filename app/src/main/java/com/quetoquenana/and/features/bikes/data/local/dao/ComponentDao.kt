package com.quetoquenana.and.features.bikes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quetoquenana.and.features.bikes.data.local.entity.ComponentEntity
import java.util.UUID

@Dao
interface ComponentDao {

    @Query("SELECT * FROM bike_components WHERE bikeId = :bikeId ORDER BY name ASC")
    suspend fun getComponentsForBike(bikeId: UUID): List<ComponentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(component: ComponentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(components: List<ComponentEntity>)

    @Query("DELETE FROM bike_components WHERE bikeId = :bikeId")
    suspend fun clearForBike(bikeId: UUID)

    @Query("DELETE FROM bike_components")
    suspend fun clearAll()
}
