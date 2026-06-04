package com.quetoquenana.and.bikes.domain.repository

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
import com.quetoquenana.and.features.bikes.domain.model.isActive
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant
import java.util.UUID

class FakeBikeRepository(
    initialBikes: List<Bike> = emptyList(),
    private val refreshedBikes: List<Bike>? = null,
    private val bikeProfileImageUrls: Map<String, String> = emptyMap(),
    private val createBikeFailure: Throwable? = null,
    private val getBikesFailure: Throwable? = null,
    private val localActiveBikesAvailable: Boolean = initialBikes.any(Bike::isActive),
    private val stravaConnectUrl: StravaConnectUrl = StravaConnectUrl(
        url = "https://www.strava.com/oauth/authorize?state=test",
        state = "test"
    ),
    private val stravaConnectionStatus: StravaConnectionStatus = StravaConnectionStatus(
        connected = false,
        status = "PENDING",
        athleteId = null,
        scope = null
    ),
    private val stravaBikes: List<StravaBike> = emptyList(),
    private val stravaFailure: Throwable? = null,
    private val stravaBikeFailuresByCall: List<Throwable> = emptyList(),
    private val bikeHistory: List<BikeHistory> = emptyList(),
    bikeMedia: List<BikeMedia> = emptyList(),
    private val addComponentFailure: Throwable? = null,
    private val componentTypes: List<ComponentType> = emptyList(),
    private val componentTypesFailure: Throwable? = null,
    private val bikeMediaFailure: Throwable? = null,
    private val uploadBikeMediaFailure: Throwable? = null
) : BikeRepository {

    private val storedBikes = initialBikes.toMutableList()
    private val storedBikeMedia = bikeMedia.toMutableList()
    private val bikesFlow = MutableStateFlow(initialBikes)
    var lastCreateRequest: CreateBikeRequest? = null
    var lastAddComponentRequest: AddComponentRequest? = null
    var lastUploadBikeMediaRequest: List<MediaUploadRequest>? = null
    var lastUploadBikeProfileImageRequest: MediaUploadRequest? = null
    var getBikesCallCount: Int = 0
    var getBikeMediaCallCount: Int = 0
    var uploadBikeMediaCallCount: Int = 0
    var uploadBikeProfileImageCallCount: Int = 0
    var getStravaBikesCallCount: Int = 0

    override suspend fun getBikeComponentTypes(): List<ComponentType> {
        componentTypesFailure?.let { throw it }
        return componentTypes
    }

    override fun observeBikes(): Flow<List<Bike>> = bikesFlow

    override suspend fun hasActiveBikesLocally(): Boolean = localActiveBikesAvailable

    override suspend fun getBikeProfileImageUrl(id: UUID): String? = bikeProfileImageUrls[id.toString()]

    override suspend fun getBikes(refresh: Boolean): List<Bike> {
        getBikesCallCount += 1
        getBikesFailure?.let { throw it }
        if (refresh && refreshedBikes != null) {
            storedBikes.clear()
            storedBikes += refreshedBikes
            bikesFlow.value = storedBikes.toList()
        }
        return storedBikes.toList()
    }

    fun emitBikes(bikes: List<Bike>) {
        storedBikes.clear()
        storedBikes += bikes
        bikesFlow.value = bikes
    }

    override suspend fun getBike(id: UUID): Bike {
        getBikesFailure?.let { throw it }
        return storedBikes.first { it.id == id }
    }

    override suspend fun getBikeHistory(id: UUID): List<BikeHistory> {
        getBikesFailure?.let { throw it }
        return bikeHistory.filter { it.bikeId == id }
    }

    override suspend fun getBikeMedia(id: UUID): List<BikeMedia> {
        getBikeMediaCallCount += 1
        bikeMediaFailure?.let { throw it }
        return storedBikeMedia.toList()
    }

    override suspend fun uploadBikeMedia(
        bikeId: UUID,
        uploads: List<MediaUploadRequest>
    ) {
        uploadBikeMediaCallCount += 1
        uploadBikeMediaFailure?.let { throw it }
        lastUploadBikeMediaRequest = uploads
        storedBikeMedia += uploads.mapIndexed { index, upload ->
            BikeMedia(
                id = seededUuid("media-${uploadBikeMediaCallCount}-$index"),
                contentType = upload.contentType.replace("image/", "IMAGE_").uppercase(),
                provider = "Cloudflare",
                name = upload.name,
                altText = upload.altText,
                url = "https://example.com/${upload.name}",
                expiresAt = Instant.parse("2026-05-15T04:26:10Z")
            )
        }
    }

    override suspend fun uploadBikeProfileImage(bikeId: UUID, upload: MediaUploadRequest) {
        uploadBikeProfileImageCallCount += 1
        uploadBikeMediaFailure?.let { throw it }
        lastUploadBikeProfileImageRequest = upload
    }

    override suspend fun createBike(request: CreateBikeRequest): Bike {
        createBikeFailure?.let { throw it }
        lastCreateRequest = request

        val bike = Bike(
            id = seededUuid("bike-${storedBikes.size + 1}"),
            name = request.name,
            type = request.type,
            status = "ACTIVE",
            isPublic = request.isPublic,
            isExternalSync = request.isExternalSync,
            brand = request.brand,
            model = request.model,
            year = request.year,
            serialNumber = request.serialNumber,
            notes = request.notes,
            odometerKm = request.odometerKm?.toDouble() ?: 0.0,
            usageTimeMinutes = request.usageTimeMinutes ?: 0,
            externalGearId = request.externalGearId,
            externalSyncProvider = request.externalSyncProvider.orEmpty()
        )
        storedBikes += bike
        bikesFlow.value = storedBikes.toList()
        return bike
    }

    override suspend fun addBikeComponent(
        bikeId: UUID,
        request: AddComponentRequest
    ): Component {
        addComponentFailure?.let { throw it }
        lastAddComponentRequest = request

        val component = Component(
            id = seededUuid("component-${request.name}"),
            type = request.type,
            name = request.name,
            status = "ACTIVE",
            brand = request.brand,
            model = request.model,
            notes = request.notes,
            odometerKm = request.odometerKm,
            usageTimeMinutes = request.usageTimeMinutes
        )

        storedBikes.replaceAll { bike ->
            if (bike.id == bikeId) {
                bike.copy(components = bike.components + component)
            } else {
                bike
            }
        }
        bikesFlow.value = storedBikes.toList()

        return component
    }

    override suspend fun getStravaConnectUrl(): StravaConnectUrl {
        stravaFailure?.let { throw it }
        return stravaConnectUrl
    }

    override suspend fun getStravaConnectionStatus(): StravaConnectionStatus {
        stravaFailure?.let { throw it }
        return stravaConnectionStatus
    }

    override suspend fun getStravaBikes(): List<StravaBike> {
        getStravaBikesCallCount += 1
        stravaFailure?.let { throw it }
        stravaBikeFailuresByCall.getOrNull(getStravaBikesCallCount - 1)?.let { throw it }
        return stravaBikes
    }

    private fun seededUuid(value: String): UUID {
        return UUID.nameUUIDFromBytes(value.toByteArray())
    }
}
