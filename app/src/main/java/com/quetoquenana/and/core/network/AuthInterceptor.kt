package com.quetoquenana.and.core.network

import com.google.common.net.HttpHeaders.AUTHORIZATION
import com.quetoquenana.and.core.utils.BEARER
import com.quetoquenana.and.core.utils.NO_AUTH
import com.quetoquenana.and.core.utils.TRUE
import com.quetoquenana.and.features.authentication.session.TokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject



class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        if (original.header(name = NO_AUTH) == TRUE) {
            return chain.proceed(request = original)
        }

        val accessToken = runBlocking { tokenProvider.getAccessToken() }

        val request = original.newBuilder().apply {
            if (!accessToken.isNullOrBlank()) {
                header(AUTHORIZATION, "$BEARER $accessToken")
            }
        }.build()

        return chain.proceed(request)
    }
}