package com.quetoquenana.and.features.appointments.data.remote.dto

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.AppointmentService
import java.math.BigDecimal
import java.util.UUID

data class ChangeAppointmentStatusResponseDto(
    val appointmentId: UUID,
    val fromStatus: String,
    val toStatus: String,
    val changedAt: String,
    val serviceOrderNumber: String?,
    val deposit: BigDecimal?
)

data class AppointmentListItemResponseDto(
    val id: UUID,
    val bikeId: UUID,
    val storeLocationId: UUID,
    val scheduledAt: String,
    val status: String
)

data class AppointmentResponseDto(
    val id: UUID,
    val bikeId: UUID,
    val storeLocationId: UUID,
    val scheduledAt: String,
    val status: String,
    val notes: String?,
    val deposit: BigDecimal?,
    val requestedServices: Set<AppointmentServiceResponseDto> = emptySet()
)

data class AppointmentServiceResponseDto(
    val id: UUID,
    val productId: UUID,
    val productNameSnapshot: String,
    val priceSnapshot: BigDecimal?
)

fun AppointmentListItemResponseDto.toDomain(): Appointment {
    return Appointment(
        id = id,
        dateText = scheduledAt.toAppointmentDateText(),
        bikeId = bikeId,
        storeLocationId = storeLocationId,
        scheduledAt = scheduledAt,
        status = status,
        bikeName = ""
    )
}

fun AppointmentResponseDto.toDomain(): Appointment {
    return Appointment(
        id = id,
        dateText = scheduledAt.toAppointmentDateText(),
        bikeId = bikeId,
        bikeName = "",
        storeLocationId = storeLocationId,
        scheduledAt = scheduledAt,
        status = status,
        notes = notes,
        deposit = deposit?.toPlainString(),
        requestedServices = requestedServices.map { it.toDomain() }
    )
}

private fun AppointmentServiceResponseDto.toDomain(): AppointmentService {
    return AppointmentService(
        id = id,
        productId = productId,
        productName = productNameSnapshot,
        price = priceSnapshot?.toPlainString()
    )
}

private fun String.toAppointmentDateText(): String {
    return substringBefore(' ')
        .substringBefore('T')
        .ifBlank { this }
}

