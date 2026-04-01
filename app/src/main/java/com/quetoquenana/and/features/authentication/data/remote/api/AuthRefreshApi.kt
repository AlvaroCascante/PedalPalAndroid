package com.quetoquenana.and.features.authentication.data.remote.api

import com.quetoquenana.and.features.authentication.data.remote.dto.RefreshTokenRequestDto
import com.quetoquenana.and.features.authentication.data.remote.dto.RefreshTokenResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthRefreshApi {

    @POST("auth/refresh")
    fun refreshToken(
        @Body request: RefreshTokenRequestDto
    ): Call<RefreshTokenResponseDto>
}