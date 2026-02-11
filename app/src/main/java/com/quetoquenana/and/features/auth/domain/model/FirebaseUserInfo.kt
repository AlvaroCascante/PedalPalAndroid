package com.quetoquenana.and.features.auth.domain.model

/**
 * Domain model representing a Firebase-authenticated user snapshot.
 */
data class FirebaseUserInfo(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val isEmailVerified: Boolean,
)
