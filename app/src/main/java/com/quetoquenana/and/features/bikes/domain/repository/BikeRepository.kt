package com.quetoquenana.and.features.bikes.domain.repository

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl

interface BikeRepository {
    suspend fun getBikes(refresh: Boolean = false): List<Bike>
    suspend fun getBike(id: String): Bike
    suspend fun getBikeHistory(id: String): List<BikeHistory>
    suspend fun createBike(request: CreateBikeRequest): Bike
    suspend fun getStravaConnectUrl(): StravaConnectUrl
    suspend fun getStravaBikes(): List<StravaBike>
}
