package com.quetoquenana.and.pedalpal.features.appointments.domain.repository

import com.quetoquenana.and.pedalpal.features.appointments.domain.model.Appointment

interface AppointmentsRepository {
    suspend fun getAppointments(): List<Appointment>
}
