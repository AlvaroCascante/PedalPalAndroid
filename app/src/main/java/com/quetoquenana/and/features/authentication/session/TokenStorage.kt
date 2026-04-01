package com.quetoquenana.and.features.authentication.session

data class StoredTokens (
    val accessToken: String,
    val refreshToken: String?
)

interface TokenStorage {
    suspend fun getTokens(): StoredTokens?
    suspend fun saveTokens(tokens: StoredTokens)
    suspend fun clear()
}