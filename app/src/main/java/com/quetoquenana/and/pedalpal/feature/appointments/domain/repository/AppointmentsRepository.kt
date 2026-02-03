package com.quetoquenana.and.pedalpal.feature.appointments.domain.repository

import com.quetoquenana.and.pedalpal.feature.appointments.domain.model.Appointment

interface AppointmentsRepository {
    suspend fun getAppointments(): List<Appointment>
}
