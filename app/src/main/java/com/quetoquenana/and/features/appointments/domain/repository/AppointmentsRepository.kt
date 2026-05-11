package com.quetoquenana.and.features.appointments.domain.repository

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest

interface AppointmentsRepository {
    suspend fun getAppointments(): List<Appointment>
    suspend fun createAppointment(request: CreateAppointmentRequest): Appointment
}
