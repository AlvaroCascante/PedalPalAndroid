package com.quetoquenana.and.features.bikes.ui

import com.quetoquenana.and.features.bikes.domain.model.BikeType

fun BikeType.toBikeTypeDisplayName(): String {
    return name.toBikeDisplayType()
}

fun String.toBikeDisplayType(): String {
    return lowercase()
        .split("_", "-", " ")
        .filter { it.isNotBlank() }
        .joinToString(separator = " ") { part ->
            part.replaceFirstChar { it.uppercase() }
        }
}


