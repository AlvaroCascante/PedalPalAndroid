package com.quetoquenana.and.features.bikes.data.local.datasource

import com.quetoquenana.and.features.bikes.data.local.dao.ComponentDao
import com.quetoquenana.and.features.bikes.data.local.entity.ComponentEntity
import javax.inject.Inject

class BikeComponentLocalDataSourceRoom @Inject constructor(
    private val componentDao: ComponentDao
) : BikeComponentLocalDataSource {

    override suspend fun getComponentsForBike(bikeId: String): List<ComponentEntity> {
        return componentDao.getComponentsForBike(bikeId = bikeId)
    }

    override suspend fun saveComponent(component: ComponentEntity) {
        componentDao.upsert(component)
    }

    override suspend fun saveComponents(components: List<ComponentEntity>) {
        componentDao.upsertAll(components)
    }

    override suspend fun clearComponentsForBike(bikeId: String) {
        componentDao.clearForBike(bikeId = bikeId)
    }

    override suspend fun clearComponents() {
        componentDao.clearAll()
    }
}
