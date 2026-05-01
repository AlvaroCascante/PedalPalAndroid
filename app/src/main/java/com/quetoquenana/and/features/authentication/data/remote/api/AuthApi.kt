package com.quetoquenana.and.features.authentication.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserRequestDto
import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface AuthApi {

    @POST("auth/firebase-registration")
    @Headers("No-Auth: true")
    suspend fun completeRegistrationFromFirebase(
        @Body request: CreateUserRequestDto,
        @Header("Authorization") authorization: String,
        @Header("X-Application-Name") contentType: String = "PEDPAL"
    ): ApiResponse<CreateUserResponseDto>

    @GET("auth/firebase-login")
    @Headers("No-Auth: true")
    suspend fun resolveFirebaseSession(
        @Header("Authorization") authorization: String,
        @Header("X-Application-Name") contentType: String = "PEDPAL"
    ): ApiResponse<CreateUserResponseDto>
}
