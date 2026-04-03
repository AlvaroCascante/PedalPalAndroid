package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.StravaBike
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

