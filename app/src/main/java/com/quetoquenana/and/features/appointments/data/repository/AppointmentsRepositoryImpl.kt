package com.quetoquenana.and.features.appointments.data.repository

import com.quetoquenana.and.features.appointments.data.remote.dataSource.AppointmentsRemoteDataSource
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository
import javax.inject.Inject

class AppointmentsRepositoryImpl @Inject constructor(
    private val remote: com.quetoquenana.and.features.appointments.data.remote.dataSource.AppointmentsRemoteDataSource
) : com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository {

    override suspend fun getAppointments(): List<com.quetoquenana.and.features.appointments.domain.model.Appointment> {
        return remote.getAppointments()
    }
}
