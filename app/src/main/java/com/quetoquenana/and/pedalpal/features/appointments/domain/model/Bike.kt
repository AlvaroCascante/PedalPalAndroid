package com.quetoquenana.and.pedalpal.features.appointments.domain.model

/**
 * Lightweight Bike model used by appointments.
 */
data class Bike(
    val id: String,
    val name: String,
    val thumbnailRes: Int? = null
)
