package com.quetoquenana.and.features.auth.data.remote.dto.response

data class RefreshTokenResponseDto(
    val accessToken: String,
    val refreshToken: String?
)