package com.quetoquenana.and.features.appointments.data.remote.dataSource

import com.quetoquenana.and.features.appointments.domain.model.Appointment

interface AppointmentsRemoteDataSource {
    suspend fun getAppointments(): List<com.quetoquenana.and.features.appointments.domain.model.Appointment>
}
