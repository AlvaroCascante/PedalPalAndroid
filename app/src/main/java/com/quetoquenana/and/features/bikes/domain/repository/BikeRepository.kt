package com.quetoquenana.and.features.bikes.domain.repository

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.AddBikeComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.BikeComponent
import com.quetoquenana.and.features.bikes.domain.model.BikeComponentType
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
import com.quetoquenana.and.features.bikes.domain.model.BikeMedia
import com.quetoquenana.and.features.bikes.domain.model.BikeMediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import kotlinx.coroutines.flow.Flow

interface BikeRepository {
    suspend fun getBikeComponentTypes(): List<BikeComponentType>
    fun observeBikes(): Flow<List<Bike>>
    suspend fun getBikes(refresh: Boolean = false): List<Bike>
    suspend fun getBike(id: String): Bike
    suspend fun getBikeHistory(id: String): List<BikeHistory>
    suspend fun getBikeMedia(id: String): List<BikeMedia>
    suspend fun uploadBikeMedia(bikeId: String, uploads: List<BikeMediaUploadRequest>)
    suspend fun createBike(request: CreateBikeRequest): Bike
    suspend fun addBikeComponent(bikeId: String, request: AddBikeComponentRequest): BikeComponent
    suspend fun getStravaConnectUrl(): StravaConnectUrl
    suspend fun getStravaConnectionStatus(): StravaConnectionStatus
    suspend fun getStravaBikes(): List<StravaBike>
}
