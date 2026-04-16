package com.quetoquenana.and.features.appointments.data.remote.dto

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.AppointmentService
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class CreateAppointmentRequestDto(
    val bikeId: String,
    val storeLocationId: String,
    val customerId: String?,
    val scheduledAt: String,
    val notes: String?,
    val requestedServices: List<RequestedServiceItemRequestDto>
)

data class RequestedServiceItemRequestDto(
    val productId: String
)

data class UpdateAppointmentRequestDto(
    val authenticatedUserId: String?,
    val customerId: String?,
    val scheduledAt: String?,
    val notes: String?
)

data class ChangeAppointmentStatusRequestDto(
    val toStatus: String,
    val customerId: String?,
    val closureReason: String?,
    val technicianId: String?,
    val note: String?,
    val deposit: BigDecimal?
)

data class ChangeAppointmentStatusResponseDto(
    val appointmentId: String,
    val fromStatus: String,
    val toStatus: String,
    val changedAt: String,
    val serviceOrderNumber: String?,
    val deposit: BigDecimal?
)

data class AppointmentListItemResponseDto(
    val id: String,
    val bikeId: String,
    val storeLocationId: String,
    val scheduledAt: String,
    val status: String
)

data class AppointmentResponseDto(
    val id: String,
    val bikeId: String,
    val storeLocationId: String,
    val scheduledAt: String,
    val status: String,
    val notes: String?,
    val deposit: BigDecimal?,
    val requestedServices: Set<AppointmentServiceResponseDto> = emptySet()
)

data class AppointmentServiceResponseDto(
    val id: String,
    val productId: String,
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
        status = status
    )
}

fun AppointmentResponseDto.toDomain(): Appointment {
    return Appointment(
        id = id,
        dateText = scheduledAt.toAppointmentDateText(),
        bikeId = bikeId,
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
    return runCatching {
        val formatter = DateTimeFormatter.ofPattern("EEE, MMM d · HH:mm")
            .withZone(ZoneId.systemDefault())
        formatter.format(Instant.parse(this))
    }.getOrElse {
        this
    }
}
