package com.quetoquenana.and.features.appointments.domain.repository

import com.quetoquenana.and.features.appointments.domain.model.Appointment

interface AppointmentsRepository {
    suspend fun getAppointments(): List<Appointment>
}
