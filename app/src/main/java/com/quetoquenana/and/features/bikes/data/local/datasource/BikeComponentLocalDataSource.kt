package com.quetoquenana.and.features.bikes.data.local.datasource

import com.quetoquenana.and.features.bikes.data.local.entity.ComponentEntity

interface BikeComponentLocalDataSource {
    suspend fun getComponentsForBike(bikeId: String): List<ComponentEntity>
    suspend fun saveComponent(component: ComponentEntity)
    suspend fun saveComponents(components: List<ComponentEntity>)
    suspend fun clearComponentsForBike(bikeId: String)
    suspend fun clearComponents()
}
