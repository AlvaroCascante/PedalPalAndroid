package com.quetoquenana.and.features.auth.data.remote.api

import com.quetoquenana.and.features.auth.data.remote.dto.request.RefreshTokenRequestDto
import com.quetoquenana.and.features.auth.data.remote.dto.response.RefreshTokenResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthRefreshApi {

    @POST("auth/refresh")
    fun refreshToken(
        @Body request: RefreshTokenRequestDto
    ): Call<RefreshTokenResponseDto>
}