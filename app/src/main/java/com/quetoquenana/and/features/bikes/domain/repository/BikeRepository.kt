package com.quetoquenana.and.features.bikes.domain.repository

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest

interface BikeRepository {
    suspend fun getBikes(refresh: Boolean = false): List<Bike>
    suspend fun createBike(request: CreateBikeRequest): Bike
}
