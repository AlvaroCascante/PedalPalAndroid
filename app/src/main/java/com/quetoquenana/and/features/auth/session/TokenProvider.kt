package com.quetoquenana.and.features.auth.session

interface TokenProvider {
    suspend fun getAccessToken(): String?
}