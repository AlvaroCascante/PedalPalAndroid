package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.BikeMedia
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BikeMediaResponseDto(
    val id: String,
    val mediaUrlResponse: List<BikeMediaDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class BikeMediaDto(
    val id: String,
    val contentType: String,
    val provider: String,
    val isPrimary: Boolean,
    val status: String,
    val name: String,
    val altText: String?,
    val url: String,
    val expiresAt: String?
)

fun BikeMediaResponseDto.toDomain(): List<BikeMedia> {
    return mediaUrlResponse
        .filter { it.contentType.startsWith(prefix = "IMAGE_") }
        .map { it.toDomain() }
}

private fun BikeMediaDto.toDomain(): BikeMedia {
    return BikeMedia(
        id = id,
        contentType = contentType,
        provider = provider,
        isPrimary = isPrimary,
        status = status,
        name = name,
        altText = altText,
        url = url,
        expiresAt = expiresAt
    )
}
