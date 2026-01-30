package com.quetoquenana.and.pedalpal.feature.login.data.remote.dto

import com.quetoquenana.and.pedalpal.feature.login.domain.model.AuthToken
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
