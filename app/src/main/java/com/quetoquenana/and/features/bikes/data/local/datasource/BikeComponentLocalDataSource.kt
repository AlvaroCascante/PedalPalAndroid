package com.quetoquenana.and.features.bikes.data.local.datasource

import com.quetoquenana.and.features.bikes.data.local.entity.ComponentEntity
import java.util.UUID

interface BikeComponentLocalDataSource {
    suspend fun getComponentsForBike(bikeId: UUID): List<ComponentEntity>
    suspend fun saveComponent(component: ComponentEntity)
    suspend fun saveComponents(components: List<ComponentEntity>)
    suspend fun clearComponentsForBike(bikeId: UUID)
    suspend fun clearComponents()
}
