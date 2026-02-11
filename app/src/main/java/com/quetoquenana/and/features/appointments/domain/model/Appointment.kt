package com.quetoquenana.and.features.appointments.domain.model

/**
 * Lightweight UI/domain model for an appointment.
 */
data class Appointment(
    val id: String,
    val dateText: String,
    val bikeId: String,
    val bikeName: String,
    val thumbnailRes: Int? = null
)
