package com.quetoquenana.and.features.bikes.data.remote.dataSource

import com.quetoquenana.and.features.bikes.data.remote.api.BikeApi
import com.quetoquenana.and.features.bikes.data.remote.api.ComponentApi
import com.quetoquenana.and.features.bikes.data.remote.api.StravaApi
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeComponentDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeHistoryDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaBikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectionStatusDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectUrlDto
import com.quetoquenana.and.features.bikes.data.remote.dto.ComponentDto
import com.quetoquenana.and.features.bikes.data.remote.dto.toDto
import com.quetoquenana.and.features.bikes.domain.model.AddComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import java.util.UUID
import javax.inject.Inject

class BikeRemoteDataSourceRetrofit @Inject constructor(
    private val bikeApi: BikeApi,
    private val componentApi: ComponentApi,
    private val stravaApi: StravaApi
) : BikeRemoteDataSource {

    override suspend fun getBikeComponentTypes(): Set<ComponentDto> {
        val response = componentApi.getComponentTypes()
        return response.data
    }

    override suspend fun getBikes(): List<BikeDto> {
        val response = bikeApi.getBikes()
        return response.data
    }

    override suspend fun getBike(id: UUID): BikeDto {
        val response = bikeApi.getBike(id = id)
        return response.data
    }

    override suspend fun getBikeHistory(id: UUID): List<BikeHistoryDto> {
        val response = bikeApi.getBikeHistory(id = id)
        return response.data
    }


    override suspend fun createBike(request: CreateBikeRequest): BikeDto {
        val response = bikeApi.createBike(request.toDto())
        return response.data
    }

    override suspend fun addBikeComponent(
        bikeId: UUID,
        request: AddComponentRequest
    ): BikeComponentDto {
        val response = bikeApi.addBikeComponent(
            id = bikeId,
            request = request.toDto()
        )
        return response.data
    }

    override suspend fun getStravaConnectUrl(): StravaConnectUrlDto {
        val response = stravaApi.getStravaConnectUrl()
        return response.data
    }

    override suspend fun getStravaConnectionStatus(): StravaConnectionStatusDto {
        val response = stravaApi.getStravaConnectionStatus()
        return response.data
    }

    override suspend fun getStravaBikes(): List<StravaBikeDto> {
        val response = stravaApi.getStravaBikes()
        return response.data
    }
}
