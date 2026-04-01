package com.quetoquenana.and.features.authentication.data.remote.dto

data class RefreshTokenResponseDto(
    val accessToken: String,
    val refreshToken: String?
)