package com.quetoquenana.and.features.bikes.domain.model

data class StravaConnectionStatus(
    val connected: Boolean,
    val status: String,
    val athleteId: Long?,
    val scope: String?
) {
    val isConnected: Boolean
        get() = connected || status.equals("CONNECTED", ignoreCase = true)
}

