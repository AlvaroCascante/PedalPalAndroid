package com.quetoquenana.and.features.bikes.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.bikes.data.remote.dto.AddBikeComponentRequestDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeHistoryDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.CreateBikeRequestDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaBikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectUrlDto
import com.quetoquenana.and.features.bikes.data.remote.dto.UpdateBikeComponentRequestDto
import com.quetoquenana.and.features.bikes.data.remote.dto.UpdateBikeComponentStatusRequestDto
import com.quetoquenana.and.features.bikes.data.remote.dto.UpdateBikeRequestDto
import com.quetoquenana.and.features.bikes.data.remote.dto.UpdateBikeStatusRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface BikeApi {

    @GET("bikes/active")
    suspend fun getBikes(): ApiResponse<List<BikeDto>>

    @GET("bikes/{id}")
    suspend fun getBike(@Path("id") id: String): ApiResponse<BikeDto>

    @GET("bikes/{id}/history")
    suspend fun getBikeHistory(@Path("id") id: String): ApiResponse<List<BikeHistoryDto>>

    @POST("bikes")
    suspend fun createBike(
        @Body request: CreateBikeRequestDto
    ): ApiResponse<BikeDto>

    @PATCH("bikes/{id}")
    suspend fun updateBike(
        @Path("id") id: String,
        @Body request: UpdateBikeRequestDto
    ): ApiResponse<BikeDto>

    @PATCH("bikes/{id}/status")
    suspend fun updateBikeStatus(
        @Path("id") id: String,
        @Body request: UpdateBikeStatusRequestDto
    ): ApiResponse<BikeDto>

    @POST("bikes/{id}/components")
    suspend fun addBikeComponent(
        @Path("id") id: String,
        @Body request: AddBikeComponentRequestDto
    ): ApiResponse<BikeDto>

    @PATCH("bikes/{id}/components/{componentId}")
    suspend fun updateBikeComponent(
        @Path("id") id: String,
        @Path("componentId") componentId: String,
        @Body request: UpdateBikeComponentRequestDto
    ): ApiResponse<BikeDto>

    @PATCH("bikes/{id}/components/{componentId}/status")
    suspend fun updateBikeComponentStatus(
        @Path("id") id: String,
        @Path("componentId") componentId: String,
        @Body request: UpdateBikeComponentStatusRequestDto
    ): ApiResponse<BikeDto>

    @PATCH("bikes/{id}/components/{componentId}/replace")
    suspend fun replaceBikeComponent(
        @Path("id") id: String,
        @Path("componentId") componentId: String,
        @Body request: AddBikeComponentRequestDto
    ): ApiResponse<BikeDto>

    @GET("strava/connect-url")
    suspend fun getStravaConnectUrl(): ApiResponse<StravaConnectUrlDto>

    @GET("strava/bikes")
    suspend fun getStravaBikes(): ApiResponse<List<StravaBikeDto>>
}
