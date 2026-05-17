package com.quetoquenana.and.features.appointments.data.remote.dataSource

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest

interface AppointmentsRemoteDataSource {
    suspend fun getAppointments(): List<Appointment>
    suspend fun getAppointment(id: String): Appointment
    suspend fun createAppointment(request: CreateAppointmentRequest): Appointment
}
