package com.quetoquenana.and.features.bikes.domain.model

data class CreateBikeRequest(
    val name: String,
    val type: String,
    val isPublic: Boolean,
    val brand: String?,
    val model: String?,
    val year: Int?,
    val serialNumber: String?,
    val notes: String?
)
