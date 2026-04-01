package com.quetoquenana.and.features.bikes.data.remote.dataSource

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest

interface BikeRemoteDataSource {
    suspend fun getBikes(): List<Bike>
    suspend fun createBike(request: CreateBikeRequest): Bike
}
