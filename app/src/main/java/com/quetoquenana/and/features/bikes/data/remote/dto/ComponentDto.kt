package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.BikeComponentType

data class ComponentDto(
    val id: String,
    val category: String,
    val code: String,
    val codeDescription: String,
    val status: String,
    val position: Int?
)

fun ComponentDto.toDomain(): BikeComponentType {
    return BikeComponentType(
        id = id,
        category = category,
        code = code,
        codeDescription = codeDescription,
        status = status,
        position = position
    )
}
