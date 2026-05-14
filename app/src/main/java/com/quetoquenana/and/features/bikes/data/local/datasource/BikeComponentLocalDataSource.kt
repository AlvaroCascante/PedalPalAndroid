package com.quetoquenana.and.features.bikes.data.local.datasource

import com.quetoquenana.and.features.bikes.data.local.entity.BikeComponentEntity

interface BikeComponentLocalDataSource {
    suspend fun getComponentsForBike(bikeId: String): List<BikeComponentEntity>
    suspend fun saveComponent(component: BikeComponentEntity)
    suspend fun saveComponents(components: List<BikeComponentEntity>)
    suspend fun clearComponentsForBike(bikeId: String)
    suspend fun clearComponents()
}
