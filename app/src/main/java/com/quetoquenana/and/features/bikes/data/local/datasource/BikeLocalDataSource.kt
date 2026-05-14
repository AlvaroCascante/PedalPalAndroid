package com.quetoquenana.and.features.bikes.data.local.datasource

import com.quetoquenana.and.features.bikes.data.local.entity.BikeEntity
import kotlinx.coroutines.flow.Flow

interface BikeLocalDataSource {
    fun observeBikes(): Flow<List<BikeEntity>>
    suspend fun getBikes(): List<BikeEntity>
    suspend fun saveBike(bike: BikeEntity)
    suspend fun saveBikes(bikes: List<BikeEntity>)
    suspend fun clearBikes()
}
