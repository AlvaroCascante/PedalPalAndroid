package com.quetoquenana.and.features.auth.data.remote.dto

import com.quetoquenana.and.features.auth.domain.model.AuthToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokensDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("expiresIn") val expiresIn: Long,
)

fun AuthTokensDto.toDomain(): com.quetoquenana.and.features.auth.domain.model.AuthToken =
    _root_ide_package_.com.quetoquenana.and.features.auth.domain.model.AuthToken(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresIn = expiresIn,
    )
