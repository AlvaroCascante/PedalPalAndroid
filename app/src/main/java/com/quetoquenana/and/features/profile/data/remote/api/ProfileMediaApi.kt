package com.quetoquenana.and.features.profile.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ProfileMediaApi {

    @GET("media/{referenceType}/{id}")
    suspend fun getMedia(
        @Path("referenceType") referenceType: String,
        @Path("id") id: String,
    ): ApiResponse<List<com.quetoquenana.and.features.profile.data.remote.dto.ProfileMediaFileResponseDto>>

    @POST("media/{id}")
    suspend fun createMedia(
        @Path("id") id: String,
        @Body request: com.quetoquenana.and.features.profile.data.remote.dto.CreateProfileMediaRequestDto,
        @Header("X-Application-Name") applicationName: String = "PEDPAL"
    ): ApiResponse<List<com.quetoquenana.and.features.profile.data.remote.dto.ProfileMediaFileResponseDto>>

    @POST("media/{id}/confirm")
    suspend fun confirmMedia(@Path("id") id: String): Response<Unit>
}


