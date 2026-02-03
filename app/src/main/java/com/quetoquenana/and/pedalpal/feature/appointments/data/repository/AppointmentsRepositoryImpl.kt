package com.quetoquenana.and.pedalpal.feature.appointments.data.repository

import com.quetoquenana.and.pedalpal.feature.appointments.data.remote.dataSource.AppointmentsRemoteDataSource
import com.quetoquenana.and.pedalpal.feature.appointments.domain.model.Appointment
import com.quetoquenana.and.pedalpal.feature.appointments.domain.repository.AppointmentsRepository
import javax.inject.Inject

class AppointmentsRepositoryImpl @Inject constructor(
    private val remote: AppointmentsRemoteDataSource
) : AppointmentsRepository {

    override suspend fun getAppointments(): List<Appointment> {
        return remote.getAppointments()
    }
}
