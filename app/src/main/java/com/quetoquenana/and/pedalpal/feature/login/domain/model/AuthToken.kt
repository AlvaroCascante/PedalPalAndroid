package com.quetoquenana.and.pedalpal.feature.auth.domain.model

/**
 * Domain model representing the token payload returned by the auth service.
 *
 * Example JSON:
 * {
 *   "accessToken": "eyJraW...",
 *   "refreshToken": "eyJraW....",
 *   "expiresIn": 604800
 * }
 */
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)