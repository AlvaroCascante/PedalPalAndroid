package com.quetoquenana.and.core.network

data class ApiResponse<T>(
    val message: String,
    val errorCode: Int = 0,
    val data: T
)