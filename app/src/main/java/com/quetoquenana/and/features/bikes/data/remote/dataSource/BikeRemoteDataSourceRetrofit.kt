package com.quetoquenana.and.features.bikes.data.remote.dataSource

import com.quetoquenana.and.core.network.networkCall
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
        return networkCall {
            componentApi.getComponentTypes()
        }
    }

    override suspend fun getBikes(): List<BikeDto> {
        return networkCall {
            bikeApi.getBikes()
        }
    }

    override suspend fun getBike(id: UUID): BikeDto {
        return networkCall {
            bikeApi.getBike(id = id)
        }
    }

    override suspend fun getBikeHistory(id: UUID): List<BikeHistoryDto> {
        return networkCall {
            bikeApi.getBikeHistory(id = id)
        }
    }

    override suspend fun createBike(request: CreateBikeRequest): BikeDto {
        return networkCall {
            bikeApi.createBike(request.toDto())
        }
    }

    override suspend fun addBikeComponent(
        bikeId: UUID,
        request: AddComponentRequest
    ): BikeComponentDto {
        return networkCall {
            bikeApi.addBikeComponent(
                id = bikeId,
                request = request.toDto()
            )
        }
    }

    override suspend fun getStravaConnectUrl(): StravaConnectUrlDto {
        return networkCall {
            stravaApi.getStravaConnectUrl()
        }
    }

    override suspend fun getStravaConnectionStatus(): StravaConnectionStatusDto {
        return networkCall {
            stravaApi.getStravaConnectionStatus()
        }
    }

    override suspend fun getStravaBikes(): List<StravaBikeDto> {
        return networkCall {
            stravaApi.getStravaBikes()
        }
    }
}
