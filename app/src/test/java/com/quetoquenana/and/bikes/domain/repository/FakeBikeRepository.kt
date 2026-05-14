package com.quetoquenana.and.bikes.domain.repository

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.AddBikeComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.BikeComponent
import com.quetoquenana.and.features.bikes.domain.model.BikeComponentType
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeBikeRepository(
    initialBikes: List<Bike> = emptyList(),
    private val createBikeFailure: Throwable? = null,
    private val getBikesFailure: Throwable? = null,
    private val stravaConnectUrl: StravaConnectUrl = StravaConnectUrl(
        url = "https://www.strava.com/oauth/authorize?state=test",
        state = "test"
    ),
    private val stravaBikes: List<StravaBike> = emptyList(),
    private val stravaFailure: Throwable? = null,
    private val bikeHistory: List<BikeHistory> = emptyList(),
    private val addComponentFailure: Throwable? = null,
    private val componentTypes: List<BikeComponentType> = emptyList(),
    private val componentTypesFailure: Throwable? = null
) : BikeRepository {

    private val storedBikes = initialBikes.toMutableList()
    private val bikesFlow = MutableStateFlow(initialBikes)
    var lastCreateRequest: CreateBikeRequest? = null
    var lastAddComponentRequest: AddBikeComponentRequest? = null
    var getBikesCallCount: Int = 0

    override suspend fun getBikeComponentTypes(): List<BikeComponentType> {
        componentTypesFailure?.let { throw it }
        return componentTypes
    }

    override fun observeBikes(): Flow<List<Bike>> = bikesFlow

    override suspend fun getBikes(refresh: Boolean): List<Bike> {
        getBikesCallCount += 1
        getBikesFailure?.let { throw it }
        return storedBikes.toList()
    }

    fun emitBikes(bikes: List<Bike>) {
        storedBikes.clear()
        storedBikes += bikes
        bikesFlow.value = bikes
    }

    override suspend fun getBike(id: String): Bike {
        getBikesFailure?.let { throw it }
        return storedBikes.first { it.id == id }
    }

    override suspend fun getBikeHistory(id: String): List<BikeHistory> {
        getBikesFailure?.let { throw it }
        return bikeHistory.filter { it.bikeId == id }
    }

    override suspend fun createBike(request: CreateBikeRequest): Bike {
        createBikeFailure?.let { throw it }
        lastCreateRequest = request

        val bike = Bike(
            id = "bike-${storedBikes.size + 1}",
            name = request.name,
            type = request.type,
            status = "ACTIVE",
            isPublic = request.isPublic,
            isExternalSync = false,
            brand = request.brand,
            model = request.model,
            year = request.year,
            serialNumber = request.serialNumber,
            notes = request.notes,
            odometerKm = 0.0,
            usageTimeMinutes = 0,
            externalGearId = null,
            externalSyncProvider = ""
        )
        storedBikes += bike
        bikesFlow.value = storedBikes.toList()
        return bike
    }

    override suspend fun addBikeComponent(
        bikeId: String,
        request: AddBikeComponentRequest
    ): BikeComponent {
        addComponentFailure?.let { throw it }
        lastAddComponentRequest = request

        val component = BikeComponent(
            id = "component-${request.name}",
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

    override suspend fun getStravaBikes(): List<StravaBike> {
        stravaFailure?.let { throw it }
        return stravaBikes
    }
}
