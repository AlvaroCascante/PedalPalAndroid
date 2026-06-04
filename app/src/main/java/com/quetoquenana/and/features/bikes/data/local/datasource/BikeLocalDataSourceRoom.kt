package com.quetoquenana.and.features.bikes.data.local.datasource

import com.quetoquenana.and.features.bikes.data.local.dao.BikeDao
import com.quetoquenana.and.features.bikes.data.local.entity.BikeEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class BikeLocalDataSourceRoom @Inject constructor(
    private val bikeDao: BikeDao
) : BikeLocalDataSource {

    override fun observeBikes(): Flow<List<BikeEntity>> = bikeDao.observeBikes()

    override suspend fun hasActiveBikes(): Boolean = bikeDao.hasActiveBikes()

    override suspend fun getBikes(): List<BikeEntity> = bikeDao.getBikes()

    override suspend fun getBikeById(id: UUID): BikeEntity? = bikeDao.getBikeById(id)

    override suspend fun saveBike(bike: BikeEntity) {
        bikeDao.upsert(bike)
    }

    override suspend fun saveBikes(bikes: List<BikeEntity>) {
        bikeDao.upsertAll(bikes)
    }

    override suspend fun clearBikes() {
        bikeDao.clearAll()
    }
}
