package com.quetoquenana.and.features.bikes.data.local.datasource

import com.quetoquenana.and.features.bikes.data.local.dao.BikeComponentDao
import com.quetoquenana.and.features.bikes.data.local.entity.BikeComponentEntity
import javax.inject.Inject

class BikeComponentLocalDataSourceRoom @Inject constructor(
    private val bikeComponentDao: BikeComponentDao
) : BikeComponentLocalDataSource {

    override suspend fun getComponentsForBike(bikeId: String): List<BikeComponentEntity> {
        return bikeComponentDao.getComponentsForBike(bikeId = bikeId)
    }

    override suspend fun saveComponent(component: BikeComponentEntity) {
        bikeComponentDao.upsert(component)
    }

    override suspend fun saveComponents(components: List<BikeComponentEntity>) {
        bikeComponentDao.upsertAll(components)
    }

    override suspend fun clearComponentsForBike(bikeId: String) {
        bikeComponentDao.clearForBike(bikeId = bikeId)
    }

    override suspend fun clearComponents() {
        bikeComponentDao.clearAll()
    }
}
