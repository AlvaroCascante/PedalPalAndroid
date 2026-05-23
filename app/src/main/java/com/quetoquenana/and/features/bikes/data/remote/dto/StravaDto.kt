package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StravaBikeDto(
    val id: String,
    val name: String,
    val nickname: String?,
    val primary: Boolean,
    val retired: Boolean,
    val distance: Double?
)

fun StravaBikeDto.toDomain(): StravaBike {
    return StravaBike(
        id = id,
        name = name,
        nickname = nickname,
        primary = primary,
        retired = retired,
        distance = distance
    )
}

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

@JsonClass(generateAdapter = true)
data class StravaConnectUrlDto(
    val url: String,
    val state: String
)

fun StravaConnectUrlDto.toDomain(): StravaConnectUrl {
    return StravaConnectUrl(
        url = url,
        state = state
    )
}
