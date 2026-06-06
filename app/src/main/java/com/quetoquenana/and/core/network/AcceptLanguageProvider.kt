package com.quetoquenana.and.core.network

import com.quetoquenana.and.core.utils.DEFAULT_LANGUAGE_TAG
import com.quetoquenana.and.core.utils.LANGUAGE_TAG_EN
import com.quetoquenana.and.core.utils.LANGUAGE_TAG_ES
import java.util.Locale
import javax.inject.Inject


class AcceptLanguageProvider @Inject constructor() {

    private val supportedLanguageTagsByLanguage = mapOf(
        LANGUAGE_TAG_EN to LANGUAGE_TAG_EN,
        LANGUAGE_TAG_ES to LANGUAGE_TAG_ES
    )

    fun getAcceptLanguage(): String {
        return resolveAcceptLanguage(locale = Locale.getDefault())
    }

    internal fun resolveAcceptLanguage(locale: Locale): String {
        val deviceLanguage = locale.language.lowercase(Locale.US)
        return supportedLanguageTagsByLanguage[deviceLanguage] ?: DEFAULT_LANGUAGE_TAG
    }
}
