package com.quetoquenana.and.features.appointments.domain.model

data class CreateAppointmentRequest(
    val bikeId: String,
    val storeLocationId: String,
    val customerId: String? = null,
    val scheduledAt: String,
    val notes: String?,
    val requestedServices: List<RequestedServiceItem>
)

data class RequestedServiceItem(
    val serviceId: String,
    val serviceType: String
)

enum class RequestedServiceType(val apiValue: String) {
    Package("Package"),
    SingleService("Single Service")
}
