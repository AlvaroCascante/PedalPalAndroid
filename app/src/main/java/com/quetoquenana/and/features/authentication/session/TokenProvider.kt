package com.quetoquenana.and.features.authentication.session

interface TokenProvider {
    suspend fun getAccessToken(): String?
}