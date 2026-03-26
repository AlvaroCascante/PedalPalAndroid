package com.quetoquenana.and.features.auth.data.remote.dto.response

import com.quetoquenana.and.features.auth.domain.model.AuthSessionResult
import com.quetoquenana.and.features.auth.domain.model.AuthUserResult
import com.quetoquenana.and.features.auth.domain.model.CreateUserResult

data class CreateUserResponse(
    val tokenResponse: CreateUserTokenResponse,
    val user: CreateUserUserResponse,
    val photoUrl: String?
)

data class CreateUserTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class CreateUserUserResponse(
    val id: String,
    val idNumber: String,
    val name: String,
    val lastname: String,
    val username: String,
    val nickname: String,
    val applicationName: String,
    val applicationCode: String
)

fun CreateUserResponse.toResult(): CreateUserResult {
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