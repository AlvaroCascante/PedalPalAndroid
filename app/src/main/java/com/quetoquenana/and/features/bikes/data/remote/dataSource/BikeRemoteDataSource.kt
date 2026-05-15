package com.quetoquenana.and.features.bikes.data.remote.dataSource

import com.quetoquenana.and.features.bikes.data.remote.dto.BikeHistoryDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeMediaResponseDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeComponentDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaBikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectUrlDto
import com.quetoquenana.and.features.bikes.data.remote.dto.SystemCodeDto
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.model.AddBikeComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.BikeMediaUploadRequest

interface BikeRemoteDataSource {
    suspend fun getBikeComponentTypes(): Set<SystemCodeDto>
    suspend fun getBikes(): List<BikeDto>
    suspend fun getBike(id: String): BikeDto
    suspend fun getBikeHistory(id: String): List<BikeHistoryDto>
    suspend fun getBikeMedia(id: String): BikeMediaResponseDto
    suspend fun createBikeMedia(bikeId: String, uploads: List<BikeMediaUploadRequest>): BikeMediaResponseDto
    suspend fun confirmBikeMedia(mediaId: String)
    suspend fun createBike(request: CreateBikeRequest): BikeDto
    suspend fun addBikeComponent(bikeId: String, request: AddBikeComponentRequest): BikeComponentDto
    suspend fun getStravaConnectUrl(): StravaConnectUrlDto
    suspend fun getStravaBikes(): List<StravaBikeDto>
}
