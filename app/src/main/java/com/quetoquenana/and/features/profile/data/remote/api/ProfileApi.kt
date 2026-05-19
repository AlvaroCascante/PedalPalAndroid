package com.quetoquenana.and.features.profile.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApi {

    @GET("users/{id}")
    suspend fun getProfile(
        @Path("id") id: String,
    ): ApiResponse<com.quetoquenana.and.features.profile.data.remote.dto.ProfileResponseDto>
}


