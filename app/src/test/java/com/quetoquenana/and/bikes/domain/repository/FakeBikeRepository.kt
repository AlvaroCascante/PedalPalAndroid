package com.quetoquenana.and.bikes.domain.repository

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository

class FakeBikeRepository(
    initialBikes: List<Bike> = emptyList(),
    private val createBikeFailure: Throwable? = null,
    private val getBikesFailure: Throwable? = null,
    private val stravaConnectUrl: StravaConnectUrl = StravaConnectUrl(
        url = "https://www.strava.com/oauth/authorize?state=test",
        state = "test"
    ),
    private val stravaBikes: List<StravaBike> = emptyList(),
    private val stravaFailure: Throwable? = null
) : BikeRepository {

    private val storedBikes = initialBikes.toMutableList()
    var lastCreateRequest: CreateBikeRequest? = null

    override suspend fun getBikes(refresh: Boolean): List<Bike> {
        getBikesFailure?.let { throw it }
        return storedBikes.toList()
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
        return bike
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

