package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StravaConnectionStatusDto(
    val connected: Boolean,
    val status: String,
    val athleteId: Long?,
    val scope: String?
)

fun StravaConnectionStatusDto.toDomain(): StravaConnectionStatus {
    return StravaConnectionStatus(
        connected = connected,
        status = status,
        athleteId = athleteId,
        scope = scope
    )
}

