package com.quetoquenana.and.features.appointments.data.remote.dto

import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import com.quetoquenana.and.features.appointments.domain.model.RequestedServiceItem
import java.math.BigDecimal
import java.util.UUID

data class CreateAppointmentRequestDto(
    val bikeId: UUID,
    val storeLocationId: UUID,
    val scheduledAt: String,
    val customerId: UUID?,
    val notes: String?,
    val requestedServices: List<RequestedServiceItemRequestDto>
)

data class RequestedServiceItemRequestDto(
    val serviceId: UUID,
    val serviceType: String
)

data class UpdateAppointmentRequestDto(
    val authenticatedUserId: UUID?,
    val customerId: UUID?,
    val scheduledAt: String?,
    val notes: String?
)

data class ChangeAppointmentStatusRequestDto(
    val toStatus: String,
    val customerId: UUID?,
    val closureReason: String?,
    val technicianId: String?,
    val note: String?,
    val deposit: BigDecimal?
)

fun CreateAppointmentRequest.toDto(): CreateAppointmentRequestDto {
    return CreateAppointmentRequestDto(
        bikeId = bikeId,
        storeLocationId = storeLocationId,
        customerId = customerId,
        scheduledAt = scheduledAt,
        notes = notes,
        requestedServices = requestedServices.map { it.toDto() }
    )
}

private fun RequestedServiceItem.toDto(): RequestedServiceItemRequestDto {
    return RequestedServiceItemRequestDto(
        serviceId = serviceId,
        serviceType = serviceType
    )
}
