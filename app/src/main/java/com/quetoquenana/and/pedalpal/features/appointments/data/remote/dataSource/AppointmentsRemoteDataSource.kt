package com.quetoquenana.and.pedalpal.features.appointments.data.remote.dataSource

import com.quetoquenana.and.pedalpal.features.appointments.domain.model.Appointment

interface AppointmentsRemoteDataSource {
    suspend fun getAppointments(): List<Appointment>
}
