package com.quetoquenana.and.pedalpal.feature.appointments.data.remote.dataSource

import com.quetoquenana.and.pedalpal.feature.appointments.domain.model.Appointment

interface AppointmentsRemoteDataSource {
    suspend fun getAppointments(): List<Appointment>
}
