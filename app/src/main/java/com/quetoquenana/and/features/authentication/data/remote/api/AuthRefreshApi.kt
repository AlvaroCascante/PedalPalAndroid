package com.quetoquenana.and.features.authentication.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserResponseDto
import com.quetoquenana.and.features.authentication.data.remote.dto.RefreshTokenRequestDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthRefreshApi {

    @POST("auth/refresh")
    fun refreshToken(
        @Body request: RefreshTokenRequestDto
    ): Call<ApiResponse<CreateUserResponseDto>>
}
