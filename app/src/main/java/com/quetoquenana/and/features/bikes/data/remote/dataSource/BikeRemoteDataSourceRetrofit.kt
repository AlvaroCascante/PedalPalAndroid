package com.quetoquenana.and.features.bikes.data.remote.dataSource

import com.quetoquenana.and.features.bikes.data.remote.api.BikeApi
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaBikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectUrlDto
import com.quetoquenana.and.features.bikes.data.remote.dto.toDto
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import javax.inject.Inject

class BikeRemoteDataSourceRetrofit @Inject constructor(
    private val bikeApi: BikeApi
) : BikeRemoteDataSource {

    override suspend fun getBikes(): List<BikeDto> {
        val response = bikeApi.getBikes()
        return response.data
    }

    override suspend fun createBike(request: CreateBikeRequest): BikeDto {
        val response = bikeApi.createBike(request.toDto())
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
