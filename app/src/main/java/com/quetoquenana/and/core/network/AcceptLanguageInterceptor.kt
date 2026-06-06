package com.quetoquenana.and.core.network

import com.quetoquenana.and.core.utils.HEADER_ACCEPT_LANGUAGE
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

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
