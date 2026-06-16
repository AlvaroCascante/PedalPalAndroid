package com.quetoquenana.and.features.bikes.domain.repository

import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.AddComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.Component
import com.quetoquenana.and.features.bikes.domain.model.ComponentType
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
import com.quetoquenana.and.features.bikes.domain.model.BikeMedia
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BikeRepository {
    suspend fun getBikeComponentTypes(): List<ComponentType>
    fun observeBikes(): Flow<List<Bike>>
    suspend fun hasActiveBikesLocally(): Boolean
    suspend fun getBikeProfileImageUrl(id: UUID): String?
    suspend fun getBikes(refresh: Boolean = false): List<Bike>
    suspend fun getBike(id: UUID): Bike
    suspend fun getBikeHistory(id: UUID): List<BikeHistory>
    suspend fun getBikeMedia(id: UUID): List<BikeMedia>
    suspend fun uploadBikeMedia(bikeId: UUID, uploads: List<MediaUploadRequest>)
    suspend fun uploadBikeProfileImage(bikeId: UUID, upload: MediaUploadRequest)
    suspend fun createBike(request: CreateBikeRequest)
    suspend fun addBikeComponent(bikeId: UUID, request: AddComponentRequest): Component
    suspend fun getStravaConnectUrl(): StravaConnectUrl
    suspend fun getStravaConnectionStatus(): StravaConnectionStatus
    suspend fun getStravaBikes(): List<StravaBike>
}
