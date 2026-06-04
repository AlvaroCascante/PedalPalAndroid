package com.quetoquenana.and.features.authentication.domain.model

import java.util.UUID

data class CreateUserResult(
    val session: AuthSessionResult,
    val user: AuthUserResult
)

data class AuthSessionResult(
    val userId: UUID?,
    val isLoggedIn: Boolean,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
)

data class AuthUserResult(
    val id: UUID,
    val username: String,
    val idNumber: String,
    val name: String,
    val lastname: String,
    val nickname: String,
    val profileCompleted: Boolean = false
)
