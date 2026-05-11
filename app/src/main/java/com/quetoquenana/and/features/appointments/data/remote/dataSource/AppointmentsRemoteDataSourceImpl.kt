package com.quetoquenana.and.features.appointments.data.remote.dataSource

import com.quetoquenana.and.features.appointments.data.remote.api.AppointmentApi
import com.quetoquenana.and.features.appointments.data.remote.dto.toDto
import com.quetoquenana.and.features.appointments.data.remote.dto.toDomain
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import javax.inject.Inject

class AppointmentsRemoteDataSourceImpl @Inject constructor(
    private val api: AppointmentApi
) : AppointmentsRemoteDataSource {

    override suspend fun getAppointments(): List<Appointment> {
        return api.getAppointments()
            .data
            .map { it.toDomain() }
    }

    override suspend fun createAppointment(request: CreateAppointmentRequest): Appointment {
        return api.createAppointment(request.toDto()).data.toDomain()
    }
}
