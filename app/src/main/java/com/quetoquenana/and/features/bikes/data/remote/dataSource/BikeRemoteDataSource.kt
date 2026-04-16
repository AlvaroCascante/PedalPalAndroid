package com.quetoquenana.and.features.bikes.data.remote.dataSource

import com.quetoquenana.and.features.bikes.data.remote.dto.BikeHistoryDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaBikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectUrlDto
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest

interface BikeRemoteDataSource {
    suspend fun getBikes(): List<BikeDto>
    suspend fun getBike(id: String): BikeDto
    suspend fun getBikeHistory(id: String): List<BikeHistoryDto>
    suspend fun createBike(request: CreateBikeRequest): BikeDto
    suspend fun getStravaConnectUrl(): StravaConnectUrlDto
    suspend fun getStravaBikes(): List<StravaBikeDto>
}
