package com.quetoquenana.and.features.authentication.data.remote.dto

import com.quetoquenana.and.features.authentication.domain.model.AuthSessionResult
import com.quetoquenana.and.features.authentication.domain.model.AuthUserResult
import com.quetoquenana.and.features.authentication.domain.model.CreateUserResult

data class CreateUserResponseDto(
    val tokenResponse: CreateUserTokenResponseDto,
    val user: CreateUserUserResponseDto,
    val photoUrl: String?
)

data class CreateUserTokenResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class CreateUserUserResponseDto(
    val id: String,
    val idNumber: String,
    val name: String,
    val lastname: String,
    val username: String,
    val nickname: String,
    val applicationName: String,
    val applicationCode: String
)

fun CreateUserResponseDto.toResult(): CreateUserResult {
    val authUserResult = AuthUserResult(
        id = user.id,
        username = user.username,
        idNumber = user.idNumber,
        name = user.name,
        lastname = user.lastname,
        nickname = user.nickname,
        photoUrl = null,        //TODO map photoUrl if available
        profileCompleted = true
    )

    val authSessionResult = AuthSessionResult(
        userId = user.username,
        isLoggedIn = true,
        accessToken = tokenResponse.accessToken,
        refreshToken = tokenResponse.refreshToken,
        expiresAt = tokenResponse.expiresIn
    )

    return CreateUserResult(
        session = authSessionResult,
        user = authUserResult
    )
}