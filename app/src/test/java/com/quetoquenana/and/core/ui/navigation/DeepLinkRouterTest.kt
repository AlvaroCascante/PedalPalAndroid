package com.quetoquenana.and.core.ui.navigation

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeepLinkRouterTest {

    private val router = DeepLinkRouter()

    @Test
    fun `parse returns StravaImport route for app link`() {
        val uri = Uri.parse("https://quetoquenana.com/strava/callback?state=xyz")
        val result = router.parse(uri)
        assertEquals(StravaImport.createRoute(fromDeepLink = true), result)
    }

    @Test
    fun `parse returns StravaImport route for custom scheme fallback`() {
        val uri = Uri.parse("pedalpal://strava-callback?state=xyz")
        val result = router.parse(uri)
        assertEquals(StravaImport.createRoute(fromDeepLink = true), result)
    }

    @Test
    fun `parse returns null for unrelated uri`() {
        val uri = Uri.parse("https://example.com/other/path")
        val result = router.parse(uri)
        assertNull(result)
    }

    @Test
    fun `parse returns null for null uri`() {
        val result = router.parse(null)
        assertNull(result)
    }
}

