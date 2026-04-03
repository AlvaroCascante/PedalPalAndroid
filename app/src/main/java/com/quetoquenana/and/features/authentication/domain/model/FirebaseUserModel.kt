package com.quetoquenana.and.features.authentication.domain.model

/**
 * Domain model representing a Firebase-authenticated user snapshot.
 */
data class FirebaseUserModel(
    val uid: String,
    val email: String,
    val displayName: String?,
    val isEmailVerified: Boolean
)
