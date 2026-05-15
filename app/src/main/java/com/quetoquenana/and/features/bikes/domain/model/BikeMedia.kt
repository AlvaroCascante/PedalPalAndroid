package com.quetoquenana.and.features.bikes.domain.model

data class BikeMedia(
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
