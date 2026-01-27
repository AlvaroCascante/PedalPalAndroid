package com.quetoquenana.and.pedalpal.feature.auth.data.remote.dto

import com.quetoquenana.and.pedalpal.feature.auth.domain.model.AuthToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokensDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("expiresIn") val expiresIn: Long,
)

fun AuthTokensDto.toDomain(): AuthToken = AuthToken(
    accessToken = accessToken,
    refreshToken = refreshToken,
    expiresIn = expiresIn,
)
