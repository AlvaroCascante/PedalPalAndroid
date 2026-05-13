package com.quetoquenana.and.core.network

import com.quetoquenana.and.features.authentication.data.local.datasource.SessionLocalDataSource
import com.quetoquenana.and.features.authentication.data.remote.api.AuthRefreshApi
import com.quetoquenana.and.features.authentication.data.remote.dto.RefreshTokenRequestDto
import com.quetoquenana.and.features.authentication.session.StoredTokens
import com.quetoquenana.and.features.authentication.session.TokenStorage
import com.squareup.moshi.Moshi
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route


private const val HEADER_AUTHORIZATION = "Authorization"
private const val HEADER_BEARER = "Bearer "
private const val MAX_RETRY_COUNT = 2
private const val TOKEN_EXPIRED_ERROR_CODE = 40101
private const val MAX_ERROR_BODY_BYTES = 4_096L

class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val authRefreshApi: AuthRefreshApi,
    moshi: Moshi
) : Authenticator {

    private val authErrorAdapter = moshi.adapter(AuthErrorResponse::class.java)

    @Synchronized
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_RETRY_COUNT) {
            return null
        }

        if (!response.isExpiredTokenResponse()) {
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
        val tokenResponse = body.data.registration.tokenResponse

        val newTokens = StoredTokens(
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken
        )

        runBlocking {
            saveTokens(newTokens = newTokens, expiresAt = tokenResponse.expiresIn)
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

    private suspend fun saveTokens(newTokens: StoredTokens, expiresAt: Long?) {
        tokenStorage.saveTokens(newTokens)

        val session = sessionLocalDataSource.getSession() ?: return
        sessionLocalDataSource.saveSession(
            session.copy(
                accessToken = newTokens.accessToken,
                refreshToken = newTokens.refreshToken,
                expiresAt = expiresAt,
                lastUpdatedAt = System.currentTimeMillis()
            )
        )
    }

    private fun Response.isExpiredTokenResponse(): Boolean {
        if (code != 401) {
            return false
        }

        val errorBody = try {
            peekBody(MAX_ERROR_BODY_BYTES).string()
        } catch (_: Exception) {
            return false
        }

        return try {
            authErrorAdapter.fromJson(errorBody)?.errorCode == TOKEN_EXPIRED_ERROR_CODE
        } catch (_: Exception) {
            false
        }
    }

    private data class AuthErrorResponse(
        val message: String?,
        val errorCode: Int?
    )
}
