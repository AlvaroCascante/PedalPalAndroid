package com.quetoquenana.and.core.network

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

private const val HEADER_ACCEPT_LANGUAGE = "Accept-Language"

class AcceptLanguageInterceptor @Inject constructor(
    private val acceptLanguageProvider: AcceptLanguageProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        if (original.header(HEADER_ACCEPT_LANGUAGE) != null) {
            return chain.proceed(original)
        }

        val request = original.newBuilder()
            .header(HEADER_ACCEPT_LANGUAGE, acceptLanguageProvider.getAcceptLanguage())
            .build()

        return chain.proceed(request)
    }
}
