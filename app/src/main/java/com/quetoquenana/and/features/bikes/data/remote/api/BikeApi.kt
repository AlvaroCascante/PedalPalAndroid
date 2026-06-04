package com.quetoquenana.and.features.bikes.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.bikes.data.remote.dto.AddComponentRequestDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeComponentDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeHistoryDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.CreateBikeRequestDto
import com.quetoquenana.and.features.bikes.data.remote.dto.UpdateComponentRequestDto
import com.quetoquenana.and.features.bikes.data.remote.dto.UpdateComponentStatusRequestDto
import com.quetoquenana.and.features.bikes.data.remote.dto.UpdateBikeRequestDto
import com.quetoquenana.and.features.bikes.data.remote.dto.UpdateBikeStatusRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface BikeApi {

    @GET("bikes/active")
    suspend fun getBikes(): ApiResponse<List<BikeDto>>

    @GET("bikes/{id}")
    suspend fun getBike(@Path("id") id: UUID): ApiResponse<BikeDto>

    @GET("bikes/{id}/history")
    suspend fun getBikeHistory(@Path("id") id: UUID): ApiResponse<List<BikeHistoryDto>>

    @POST("bikes")
    suspend fun createBike(
        @Body request: CreateBikeRequestDto
    ): ApiResponse<BikeDto>

    @PATCH("bikes/{id}")
    suspend fun updateBike(
        @Path("id") id: UUID,
        @Body request: UpdateBikeRequestDto
    ): ApiResponse<BikeDto>

    @PATCH("bikes/{id}/status")
    suspend fun updateBikeStatus(
        @Path("id") id: UUID,
        @Body request: UpdateBikeStatusRequestDto
    ): ApiResponse<BikeDto>

    @POST("bikes/{id}/components")
    suspend fun addBikeComponent(
        @Path("id") id: UUID,
        @Body request: AddComponentRequestDto
    ): ApiResponse<BikeComponentDto>

    @PATCH("bikes/{id}/components/{componentId}")
    suspend fun updateBikeComponent(
        @Path("id") id: UUID,
        @Path("componentId") componentId: UUID,
        @Body request: UpdateComponentRequestDto
    ): ApiResponse<BikeDto>

    @PATCH("bikes/{id}/components/{componentId}/status")
    suspend fun updateBikeComponentStatus(
        @Path("id") id: UUID,
        @Path("componentId") componentId: UUID,
        @Body request: UpdateComponentStatusRequestDto
    ): ApiResponse<BikeDto>

    @PATCH("bikes/{id}/components/{componentId}/replace")
    suspend fun replaceBikeComponent(
        @Path("id") id: UUID,
        @Path("componentId") componentId: UUID,
        @Body request: AddComponentRequestDto
    ): ApiResponse<BikeDto>
}
