package com.quetoquenana.and.features.home.domain.fakes

import com.quetoquenana.and.features.bikes.domain.model.AddComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
import com.quetoquenana.and.features.bikes.domain.model.BikeMedia
import com.quetoquenana.and.features.bikes.domain.model.Component
import com.quetoquenana.and.features.bikes.domain.model.ComponentType
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class FakeBikeRepository(
    private val hasActiveBikesLocallyResult: Boolean = false,
    private val bikes: List<Bike> = emptyList()
) : BikeRepository {
    override suspend fun getBikeComponentTypes(): List<ComponentType> = emptyList()
    override fun observeBikes(): Flow<List<Bike>> = flowOf(bikes)
    override suspend fun hasActiveBikesLocally(): Boolean = hasActiveBikesLocallyResult
    override suspend fun getBikeProfileImageUrl(id: UUID): String? = null
    override suspend fun getBikes(refresh: Boolean): List<Bike> = bikes
    override suspend fun getBike(id: UUID): Bike =
        bikes.firstOrNull { it.id == id } ?: throw NoSuchElementException("Bike not found: $id")
    override suspend fun getBikeHistory(id: UUID): List<BikeHistory> = emptyList()
    override suspend fun getBikeMedia(id: UUID): List<BikeMedia> = emptyList()
    override suspend fun uploadBikeMedia(bikeId: UUID, uploads: List<MediaUploadRequest>) = Unit
    override suspend fun uploadBikeProfileImage(bikeId: UUID, upload: MediaUploadRequest) = Unit
    override suspend fun createBike(request: CreateBikeRequest) = Unit
    override suspend fun addBikeComponent(bikeId: UUID, request: AddComponentRequest): Component =
        Component(
            id = UUID.randomUUID(),
            type = "CHAIN",
            name = "component",
            status = "ACTIVE",
            brand = null,
            model = null,
            notes = null,
            odometerKm = 0,
            usageTimeMinutes = 0
        )
    override suspend fun getStravaConnectUrl(): StravaConnectUrl =
        StravaConnectUrl(url = "", state = "")
    override suspend fun getStravaConnectionStatus(): StravaConnectionStatus =
        StravaConnectionStatus(
            connected = false,
            status = "",
            athleteId = null,
            scope = null
        )
    override suspend fun getStravaBikes(): List<StravaBike> = emptyList()
}
