package com.quetoquenana.and.core.network

import com.quetoquenana.and.features.auth.data.remote.dto.response.CreateUserResponse

data class ApiResponse<T>(
    val message: String,
    val errorCode: Int = 0,
    val data: T
)

sealed class ApiResponseData {
    data class CreateUserApiResponse(val registration: CreateUserResponse) : ApiResponseData()
}