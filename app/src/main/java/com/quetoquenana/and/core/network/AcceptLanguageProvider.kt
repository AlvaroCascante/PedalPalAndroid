package com.quetoquenana.and.core.network

import java.util.Locale
import javax.inject.Inject

private const val DEFAULT_LANGUAGE_TAG = "en"

class AcceptLanguageProvider @Inject constructor() {

    private val supportedLanguageTagsByLanguage = mapOf(
        "en" to "en",
        "es" to "es"
    )

    fun getAcceptLanguage(): String {
        return resolveAcceptLanguage(locale = Locale.getDefault())
    }

    internal fun resolveAcceptLanguage(locale: Locale): String {
        val deviceLanguage = locale.language.lowercase(Locale.US)
        return supportedLanguageTagsByLanguage[deviceLanguage] ?: DEFAULT_LANGUAGE_TAG
    }
}
