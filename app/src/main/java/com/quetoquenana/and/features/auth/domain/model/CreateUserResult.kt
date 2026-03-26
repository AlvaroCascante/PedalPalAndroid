package com.quetoquenana.and.features.auth.domain.model

data class CreateUserResult(
    val session: AuthSessionResult,
    val user: AuthUserResult
)

data class AuthSessionResult(
    val userId: String?,
    val isLoggedIn: Boolean,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
)

data class AuthUserResult(
    val id: String,
    val username: String,
    val idNumber: String,
    val name: String,
    val lastname: String,
    val nickname: String,
    val profileCompleted: Boolean = false,
    val photoUrl: String?
)
