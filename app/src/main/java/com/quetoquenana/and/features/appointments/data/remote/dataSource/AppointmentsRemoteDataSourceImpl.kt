package com.quetoquenana.and.features.appointments.data.remote.dataSource

import com.quetoquenana.and.features.appointments.data.remote.api.AppointmentApi
import com.quetoquenana.and.features.appointments.data.remote.dto.toDomain
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import javax.inject.Inject

class AppointmentsRemoteDataSourceImpl @Inject constructor(
    private val api: AppointmentApi
) : AppointmentsRemoteDataSource {

    override suspend fun getAppointments(): List<Appointment> {
        return api.getAppointments()
            .data
            .map { it.toDomain() }
    }
}
