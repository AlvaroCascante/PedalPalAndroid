package com.quetoquenana.and.core.network

import com.quetoquenana.and.features.auth.data.remote.api.AuthRefreshApi
import com.quetoquenana.and.features.auth.data.remote.dto.request.RefreshTokenRequestDto
import com.quetoquenana.and.features.auth.session.StoredTokens
import com.quetoquenana.and.features.auth.session.TokenStorage
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route


private const val HEADER_AUTHORIZATION = "Authorization"
private const val HEADER_BEARER = "Bearer "
private const val MAX_RETRY_COUNT = 2

class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val authRefreshApi: AuthRefreshApi
) : Authenticator {

    @Synchronized
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_RETRY_COUNT) {
            return null
        }

        val currentTokens = runBlocking { tokenStorage.getTokens() } ?: return null
        val requestAccessToken = response.request.header(HEADER_AUTHORIZATION)
            ?.removePrefix(HEADER_BEARER)
            ?.trim()

        val latestAccessToken = currentTokens.accessToken

        if (!requestAccessToken.isNullOrBlank() && requestAccessToken != latestAccessToken) {
            return response.request.newBuilder()
                .header(HEADER_AUTHORIZATION, value = HEADER_BEARER + latestAccessToken)
                .build()
        }

        val refreshToken = currentTokens.refreshToken ?: return null

        val refreshResponse = try {
            authRefreshApi.refreshToken(
                RefreshTokenRequestDto(refreshToken = refreshToken)
            ).execute()
        } catch (_: Exception) {
            return null
        }

        if (!refreshResponse.isSuccessful) {
            runBlocking { tokenStorage.clear() }
            return null
        }

        val body = refreshResponse.body() ?: return null

        val newTokens = StoredTokens(
            accessToken = body.accessToken,
            refreshToken = body.refreshToken ?: refreshToken
        )

        runBlocking {
            tokenStorage.saveTokens(newTokens)
        }

        return response.request.newBuilder()
            .header(name = HEADER_AUTHORIZATION, value = HEADER_BEARER + newTokens.accessToken)
            .build()
    }

    private fun responseCount(response: Response): Int {
        var current: Response? = response
        var result = 1
        while (current?.priorResponse != null) {
            result++
            current = current.priorResponse
        }
        return result
    }
}