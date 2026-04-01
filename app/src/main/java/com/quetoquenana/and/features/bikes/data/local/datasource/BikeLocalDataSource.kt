package com.quetoquenana.and.features.bikes.data.local.datasource

import com.quetoquenana.and.features.bikes.data.local.entity.BikeEntity

interface BikeLocalDataSource {
    suspend fun getBikes(): List<BikeEntity>
    suspend fun saveBike(bike: BikeEntity)
    suspend fun saveBikes(bikes: List<BikeEntity>)
    suspend fun replaceBikes(bikes: List<BikeEntity>)
}
