package com.quetoquenana.and.features.profile.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.profile.data.remote.dto.ProfileResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.UUID

interface ProfileApi {

    @GET("users/{id}")
    suspend fun getProfile(
        @Path("id") id: UUID,
    ): ApiResponse<ProfileResponseDto>
}


