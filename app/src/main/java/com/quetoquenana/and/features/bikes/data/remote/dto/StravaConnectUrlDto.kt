package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import com.squareup.moshi.JsonClass

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

