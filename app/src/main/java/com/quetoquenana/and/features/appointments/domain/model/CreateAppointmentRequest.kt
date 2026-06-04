package com.quetoquenana.and.features.appointments.domain.model

import java.util.UUID

data class CreateAppointmentRequest(
    val bikeId: UUID,
    val storeLocationId: UUID,
    val customerId: UUID? = null,
    val scheduledAt: String,
    val notes: String?,
    val requestedServices: List<RequestedServiceItem>
)

data class RequestedServiceItem(
    val serviceId: UUID,
    val serviceType: String
)

enum class RequestedServiceType(val apiValue: String) {
    Package("Package"),
    SingleService("Single Service")
}
