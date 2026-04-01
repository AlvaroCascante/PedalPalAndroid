package com.quetoquenana.and.features.authentication.domain.model

data class CreateUserRequest(
    val idNumber: String,
    val name: String,
    val lastname: String,
    val nickname: String,
)

