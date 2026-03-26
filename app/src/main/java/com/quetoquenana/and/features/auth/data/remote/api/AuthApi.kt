package com.quetoquenana.and.features.auth.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.core.network.ApiResponseData
import com.quetoquenana.and.features.auth.data.remote.dto.request.CreateUserRequestDto
import retrofit2.http.Body
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
    ): ApiResponse<ApiResponseData.CreateUserApiResponse>
}