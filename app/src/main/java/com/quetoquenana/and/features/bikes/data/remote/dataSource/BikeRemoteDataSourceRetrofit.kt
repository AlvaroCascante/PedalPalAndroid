package com.quetoquenana.and.features.bikes.data.remote.dataSource

import com.quetoquenana.and.features.bikes.data.remote.api.BikeApi
import com.quetoquenana.and.features.bikes.data.remote.dto.toDomain
import com.quetoquenana.and.features.bikes.data.remote.dto.toDto
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import javax.inject.Inject

class BikeRemoteDataSourceRetrofit @Inject constructor(
    private val bikeApi: BikeApi
) : BikeRemoteDataSource {

    override suspend fun getBikes(): List<Bike> {
        return bikeApi.getBikes().map { it.toDomain() }
    }

    override suspend fun createBike(request: CreateBikeRequest): Bike {
        return bikeApi.createBike(request.toDto()).toDomain()
    }
}
