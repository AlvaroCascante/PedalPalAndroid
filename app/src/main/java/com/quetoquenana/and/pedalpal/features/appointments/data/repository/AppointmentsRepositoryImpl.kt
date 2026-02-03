package com.quetoquenana.and.pedalpal.features.appointments.data.repository

import com.quetoquenana.and.pedalpal.features.appointments.data.remote.dataSource.AppointmentsRemoteDataSource
import com.quetoquenana.and.pedalpal.features.appointments.domain.model.Appointment
import com.quetoquenana.and.pedalpal.features.appointments.domain.repository.AppointmentsRepository
import javax.inject.Inject

class AppointmentsRepositoryImpl @Inject constructor(
    private val remote: AppointmentsRemoteDataSource
) : AppointmentsRepository {

    override suspend fun getAppointments(): List<Appointment> {
        return remote.getAppointments()
    }
}
