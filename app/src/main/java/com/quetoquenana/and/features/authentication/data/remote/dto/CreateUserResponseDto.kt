package com.quetoquenana.and.features.authentication.data.remote.dto

import com.quetoquenana.and.features.authentication.domain.model.AuthSessionResult
import com.quetoquenana.and.features.authentication.domain.model.AuthUserResult
import com.quetoquenana.and.features.authentication.domain.model.CreateUserResult
import java.util.UUID

data class CreateUserResponseDto(
    val registration: CreateUserDataResponseDto
)

data class CreateUserDataResponseDto(
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
    val userId: UUID,
    val idNumber: String,
    val name: String,
    val lastname: String,
    val username: String,
    val nickname: String,
    val applicationName: String,
    val applicationCode: String
)

fun CreateUserDataResponseDto.toResult(): CreateUserResult {
    val authUserResult = AuthUserResult(
        id = user.userId,
        username = user.username,
        idNumber = user.idNumber,
        name = user.name,
        lastname = user.lastname,
        nickname = user.nickname,
        profileCompleted = true
    )

    val authSessionResult = AuthSessionResult(
        userId = user.userId,
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