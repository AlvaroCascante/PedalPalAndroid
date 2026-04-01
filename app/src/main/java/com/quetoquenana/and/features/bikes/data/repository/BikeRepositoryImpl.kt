package com.quetoquenana.and.features.bikes.data.repository

import com.quetoquenana.and.features.bikes.data.local.datasource.BikeLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.entity.toDomain
import com.quetoquenana.and.features.bikes.data.local.entity.toEntity
import com.quetoquenana.and.features.bikes.data.remote.datasource.BikeRemoteDataSource
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject

class BikeRepositoryImpl @Inject constructor(
    private val local: BikeLocalDataSource,
    private val remote: BikeRemoteDataSource
) : BikeRepository {

    override suspend fun getBikes(): List<Bike> {
        val localBikes = local.getBikes()
        if (localBikes.isNotEmpty()) {
            return localBikes.map { it.toDomain() }
        }

        val remoteBikes = remote.getBikes()
        val now = System.currentTimeMillis()
        local.replaceBikes(remoteBikes.map { it.toEntity(now) })
        return remoteBikes
    }

    override suspend fun createBike(request: CreateBikeRequest): Bike {
        val bike = remote.createBike(request)
        local.saveBike(bike.toEntity(currentTimeMillis = System.currentTimeMillis()))
        return bike
    }
}
