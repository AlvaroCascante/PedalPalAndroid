package com.quetoquenana.and.features.auth.session

import jakarta.inject.Inject

class TokenProviderImpl @Inject constructor(
    private val tokenStorage: TokenStorage
) : TokenProvider {

    override suspend fun getAccessToken(): String? {
        return tokenStorage.getTokens()?.accessToken
    }
}