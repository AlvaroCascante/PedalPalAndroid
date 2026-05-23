package com.quetoquenana.and.core.media.data.remote.api

import com.quetoquenana.and.core.media.domain.model.CreateMediaRequestDto
import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface MediaApi {

    @GET("media/{referenceType}/{id}")
    suspend fun getMedia(
        @Path("referenceType") referenceType: String,
        @Path("id") id: String,
    ): ApiResponse<List<MediaFileResponseDto>>

    @POST("media/{id}")
    suspend fun createMedia(
        @Path("id") id: String,
        @Body request: CreateMediaRequestDto,
        @Header("X-Application-Name") applicationName: String = "PEDPAL",
    ): ApiResponse<List<MediaFileResponseDto>>

    @POST("media/{id}/confirm")
    suspend fun confirmMedia(@Path("id") id: String): Response<Unit>
}

