package com.quetoquenana.and.features.appointments.domain.model

/**
 * Lightweight UI/domain model for an appointment.
 */
data class Appointment(
    val id: String,
    val dateText: String,
    val bikeId: String,
    val bikeName: String? = null,
    val storeLocationId: String? = null,
    val scheduledAt: String? = null,
    val status: String? = null,
    val notes: String? = null,
    val deposit: String? = null,
    val requestedServices: List<AppointmentService> = emptyList(),
    val thumbnailRes: Int? = null
)

data class AppointmentService(
    val id: String,
    val productId: String,
    val productName: String,
    val price: String?
)
