package com.quetoquenana.and.core.network

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class AcceptLanguageProviderTest {

    private val provider = AcceptLanguageProvider()

    @Test
    fun `resolveAcceptLanguage returns es for Spanish locale`() {
        val result = provider.resolveAcceptLanguage(Locale.forLanguageTag("es-CR"))

        assertEquals("es", result)
    }

    @Test
    fun `resolveAcceptLanguage returns en for English locale`() {
        val result = provider.resolveAcceptLanguage(Locale.forLanguageTag("en-US"))

        assertEquals("en", result)
    }

    @Test
    fun `resolveAcceptLanguage defaults to en for unsupported locale`() {
        val result = provider.resolveAcceptLanguage(Locale.forLanguageTag("fr-FR"))

        assertEquals("en", result)
    }
}
