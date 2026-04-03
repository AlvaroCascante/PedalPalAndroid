package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest

data class CreateBikeRequestDto(
    val name: String,
    val type: String,
    val isPublic: Boolean,
    val brand: String?,
    val model: String?,
    val year: Int?,
    val serialNumber: String?,
    val notes: String?
)

fun CreateBikeRequest.toDto(): CreateBikeRequestDto {
    return CreateBikeRequestDto(
        name = name,
        type = type,
        isPublic = isPublic,
        brand = brand,
        model = model,
        year = year,
        serialNumber = serialNumber,
        notes = notes
    )
}
