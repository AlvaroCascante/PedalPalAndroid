package com.quetoquenana.and.core.ui.navigation

import android.net.Uri
import com.quetoquenana.and.core.utils.CUSTOM_HOST_STRAVA_CALLBACK
import com.quetoquenana.and.core.utils.DEEPLINK_PATH_STRAVA_CALLBACK
import com.quetoquenana.and.core.utils.HOST
import com.quetoquenana.and.core.utils.SCHEMA_HTTPS
import com.quetoquenana.and.core.utils.SCHEMA_PEDALPAL
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
        if (scheme == SCHEMA_HTTPS && host == HOST && uri.path?.startsWith(prefix = DEEPLINK_PATH_STRAVA_CALLBACK) == true) {
            return StravaImport.createRoute(fromDeepLink = true)
        }

        // Custom scheme fallback
        if (scheme == SCHEMA_PEDALPAL && host == CUSTOM_HOST_STRAVA_CALLBACK) {
            return StravaImport.createRoute(fromDeepLink = true)
        }

        return null
    }
}


