package com.quetoquenana.and.features.appointments.domain.model

import java.util.UUID

/**
 * Lightweight UI/domain model for an appointment.
 */
data class Appointment(
    val id: UUID,
    val dateText: String,
    val bikeId: UUID,
    val bikeName: String,
    val storeLocationId: UUID? = null,
    val storeLocationName: String? = null,
    val currency: String? = null,
    val scheduledAt: String? = null,
    val status: String? = null,
    val notes: String? = null,
    val deposit: String? = null,
    val requestedServices: List<AppointmentService> = emptyList(),
    val thumbnailRes: Int? = null
)

data class AppointmentService(
    val id: UUID,
    val productId: UUID,
    val productName: String,
    val price: String?
)
