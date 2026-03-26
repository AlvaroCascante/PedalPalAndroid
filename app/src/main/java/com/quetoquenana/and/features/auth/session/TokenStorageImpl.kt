package com.quetoquenana.and.features.auth.session

import jakarta.inject.Inject

class TokenStorageImpl @Inject constructor() : TokenStorage {

    private var storedTokens: StoredTokens? = null

    override suspend fun getTokens(): StoredTokens? {
        return storedTokens
    }

    override suspend fun saveTokens(tokens: StoredTokens) {
        storedTokens = tokens
    }

    override suspend fun clear() {
        storedTokens = null
    }
}