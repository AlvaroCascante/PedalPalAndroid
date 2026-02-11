package com.quetoquenana.and.features.auth.domain.model

/**
 * Domain models for the backend create-user request shape.
 */
data class BackendPerson(
    val idNumber: String,
    val name: String,
    val lastname: String,
)

data class BackendUser(
    val username: String,
    val nickname: String,
    val person: BackendPerson,
)

data class BackendCreateUserRequest(
    val user: BackendUser,
    val roleName: String,
)
