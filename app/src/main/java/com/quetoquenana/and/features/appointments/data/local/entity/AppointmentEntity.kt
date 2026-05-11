package com.quetoquenana.and.features.appointments.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.AppointmentService

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val id: String,
    val dateText: String,
    val bikeId: String,
    val bikeName: String?,
    val storeLocationId: String?,
    val scheduledAt: String?,
    val status: String?,
    val notes: String?,
    val deposit: String?,
    val updatedAt: Long
)

@Entity(
    tableName = "appointment_services",
    foreignKeys = [
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("appointmentId")]
)
data class AppointmentServiceEntity(
    @PrimaryKey val id: String,
    val appointmentId: String,
    val productId: String,
    val productName: String,
    val price: String?
)

fun AppointmentEntity.toDomain(services: List<AppointmentServiceEntity>): Appointment {
    return Appointment(
        id = id,
        dateText = dateText,
        bikeId = bikeId,
        bikeName = bikeName,
        storeLocationId = storeLocationId,
        scheduledAt = scheduledAt,
        status = status,
        notes = notes,
        deposit = deposit,
        requestedServices = services.map { it.toDomain() }
    )
}

fun Appointment.toEntity(currentTimeMillis: Long): AppointmentEntity {
    return AppointmentEntity(
        id = id,
        dateText = dateText,
        bikeId = bikeId,
        bikeName = bikeName,
        storeLocationId = storeLocationId,
        scheduledAt = scheduledAt,
        status = status,
        notes = notes,
        deposit = deposit,
        updatedAt = currentTimeMillis
    )
}

fun AppointmentServiceEntity.toDomain(): AppointmentService {
    return AppointmentService(
        id = id,
        productId = productId,
        productName = productName,
        price = price
    )
}

fun AppointmentService.toEntity(appointmentId: String): AppointmentServiceEntity {
    return AppointmentServiceEntity(
        id = id,
        appointmentId = appointmentId,
        productId = productId,
        productName = productName,
        price = price
    )
}
