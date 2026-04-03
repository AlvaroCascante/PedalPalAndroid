package com.quetoquenana.and.features.bikes.data.repository

import com.quetoquenana.and.features.bikes.data.local.datasource.BikeLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.entity.toDomain
import com.quetoquenana.and.features.bikes.data.local.entity.toEntity
import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeRemoteDataSource
import com.quetoquenana.and.features.bikes.data.remote.dto.toDomain
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject

class BikeRepositoryImpl @Inject constructor(
    private val local: BikeLocalDataSource,
    private val remote: BikeRemoteDataSource
) : BikeRepository {

    override suspend fun getBikes(refresh: Boolean  ): List<Bike> {
        if (refresh) {
            val bikes = remote.getBikes().map { it.toDomain() }
            val now = System.currentTimeMillis()
            local.saveBikes(bikes.map { it.toEntity(currentTimeMillis = now) })
        }
        val localBikes = local.getBikes().map { it.toDomain() }

        return localBikes
    }

    override suspend fun createBike(request: CreateBikeRequest): Bike {
        val bike = remote.createBike(request).toDomain()
        local.saveBike(bike.toEntity(currentTimeMillis = System.currentTimeMillis()))
        return bike
    }

    override suspend fun getStravaConnectUrl(): StravaConnectUrl {
        return remote.getStravaConnectUrl().toDomain()
    }

    override suspend fun getStravaBikes(): List<StravaBike> {
        return remote.getStravaBikes().map { it.toDomain() }
    }
}
