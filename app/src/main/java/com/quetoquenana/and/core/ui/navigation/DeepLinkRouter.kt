package com.quetoquenana.and.core.ui.navigation

import android.net.Uri
import javax.inject.Inject

/**
 * Lightweight deep link parser/router that maps incoming URIs to internal navigation routes.
 * Keep this file small — parsing rules for new providers (Strava, Google, etc.) can be added here.
 */
class DeepLinkRouter @Inject constructor() {
    fun parse(uri: Uri?): String? {
        if (uri == null) return null

        val scheme = uri.scheme
        val host = uri.host

        // App Link (https) for quetoquenana.com
        if (scheme == "https" && host == "quetoquenana.com" && uri.path?.startsWith("/strava/callback") == true) {
            return StravaImport.createRoute(fromDeepLink = true)
        }

        // Custom scheme fallback
        if (scheme == "pedalpal" && host == "strava-callback") {
            return StravaImport.createRoute(fromDeepLink = true)
        }

        return null
    }
}


