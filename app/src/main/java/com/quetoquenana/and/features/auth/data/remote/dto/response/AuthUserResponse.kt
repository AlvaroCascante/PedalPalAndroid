package com.quetoquenana.and.features.auth.data.remote.dto.response

data class AuthUserResponse(
    val id: String,
    val name: String,
    val email: String?,
    val photoUrl: String?,
    val profileCompleted: Boolean
)