package com.quetoquenana.and.features.bikes.data.remote.dataSource

import com.quetoquenana.and.features.bikes.data.remote.api.BikeApi
import com.quetoquenana.and.features.bikes.data.remote.api.ComponentTypeApi
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeComponentDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeHistoryDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeMediaResponseDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaBikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectUrlDto
import com.quetoquenana.and.features.bikes.data.remote.dto.ComponentDto
import com.quetoquenana.and.features.bikes.data.remote.dto.toCreateBikeMediaRequestDto
import com.quetoquenana.and.features.bikes.data.remote.dto.toDto
import com.quetoquenana.and.features.bikes.domain.model.AddBikeComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.BikeMediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import javax.inject.Inject

class BikeRemoteDataSourceRetrofit @Inject constructor(
    private val bikeApi: BikeApi,
    private val componentApi: ComponentTypeApi
) : BikeRemoteDataSource {

    override suspend fun getBikeComponentTypes(): Set<ComponentDto> {
        val response = componentApi.getComponentTypes()
        return response.data
    }

    override suspend fun getBikes(): List<BikeDto> {
        val response = bikeApi.getBikes()
        return response.data
    }

    override suspend fun getBike(id: String): BikeDto {
        val response = bikeApi.getBike(id = id)
        return response.data
    }

    override suspend fun getBikeHistory(id: String): List<BikeHistoryDto> {
        val response = bikeApi.getBikeHistory(id = id)
        return response.data
    }

    override suspend fun getBikeMedia(id: String): BikeMediaResponseDto {
        val response = bikeApi.getBikeMedia(id = id)
        return response.data
    }

    override suspend fun createBikeMedia(
        bikeId: String,
        uploads: List<BikeMediaUploadRequest>
    ): BikeMediaResponseDto {
        val response = bikeApi.createBikeMedia(
            id = bikeId,
            request = uploads.toCreateBikeMediaRequestDto()
        )
        return response.data
    }

    override suspend fun confirmBikeMedia(mediaId: String) {
        val response = bikeApi.confirmBikeMedia(id = mediaId)
        if (!response.isSuccessful) {
            throw IllegalStateException("Unable to confirm uploaded media")
        }
    }

    override suspend fun createBike(request: CreateBikeRequest): BikeDto {
        val response = bikeApi.createBike(request.toDto())
        return response.data
    }

    override suspend fun addBikeComponent(
        bikeId: String,
        request: AddBikeComponentRequest
    ): BikeComponentDto {
        val response = bikeApi.addBikeComponent(
            id = bikeId,
            request = request.toDto()
        )
        return response.data
    }

    override suspend fun getStravaConnectUrl(): StravaConnectUrlDto {
        val response = bikeApi.getStravaConnectUrl()
        return response.data
    }

    override suspend fun getStravaBikes(): List<StravaBikeDto> {
        val response = bikeApi.getStravaBikes()
        return response.data
    }
}
