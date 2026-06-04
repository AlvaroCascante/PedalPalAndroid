package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.AddComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.ComponentType
import java.util.UUID

data class ComponentDto(
    val id: UUID,
    val category: String,
    val code: String,
    val codeDescription: String,
    val status: String,
    val position: Int?
)

fun ComponentDto.toDomain(): ComponentType {
    return ComponentType(
        id = id,
        category = category,
        code = code,
        codeDescription = codeDescription,
        status = status,
        position = position
    )
}


data class AddComponentRequestDto(
    val name: String,
    val type: String,
    val brand: String?,
    val model: String?,
    val notes: String?,
    val odometerKm: Int,
    val usageTimeMinutes: Int
)

fun AddComponentRequest.toDto(): AddComponentRequestDto {
    return AddComponentRequestDto(
        name = name,
        type = type,
        brand = brand,
        model = model,
        notes = notes,
        odometerKm = odometerKm,
        usageTimeMinutes = usageTimeMinutes
    )
}

data class UpdateComponentRequestDto(
    val name: String?,
    val type: String?,
    val brand: String?,
    val model: String?,
    val notes: String?,
    val odometerKm: Int?,
    val usageTimeMinutes: Int?
)

data class UpdateComponentStatusRequestDto(
    val status: String
)