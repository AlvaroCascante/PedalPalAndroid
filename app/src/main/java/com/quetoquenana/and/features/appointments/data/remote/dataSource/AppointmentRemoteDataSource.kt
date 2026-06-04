package com.quetoquenana.and.features.appointments.data.remote.dataSource

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import java.util.UUID

interface AppointmentRemoteDataSource {
    suspend fun getAppointments(): List<Appointment>
    suspend fun getAppointment(id: UUID): Appointment
    suspend fun createAppointment(request: CreateAppointmentRequest): Appointment
}
