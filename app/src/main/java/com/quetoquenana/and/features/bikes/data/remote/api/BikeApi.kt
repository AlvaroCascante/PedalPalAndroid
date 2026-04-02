package com.quetoquenana.and.features.bikes.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.CreateBikeRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BikeApi {

    @GET("bikes/active")
    suspend fun getBikes(): ApiResponse<List<BikeDto>>

    @POST("bikes")
    suspend fun createBike(
        @Body request: CreateBikeRequestDto
    ): ApiResponse<BikeDto>
}
