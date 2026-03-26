package com.quetoquenana.and.features.auth.domain.model

data class CreateUserRequest(
    val idNumber: String,
    val name: String,
    val lastname: String,
    val nickname: String,
)

