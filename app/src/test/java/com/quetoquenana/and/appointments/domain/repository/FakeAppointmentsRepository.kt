package com.quetoquenana.and.appointments.domain.repository

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAppointmentsRepository(
    private val appointments: List<Appointment> = emptyList(),
    private val failure: Throwable? = null
) : AppointmentsRepository {

    var getAppointmentsCalled = false

    override suspend fun getAppointments(): List<Appointment> {
        getAppointmentsCalled = true
        failure?.let { throw it }
        return appointments
    }

    override fun observeAppointments(): Flow<List<Appointment>> {
        return flowOf(value = appointments)
    }

    override suspend fun createAppointment(request: CreateAppointmentRequest): Appointment {
        failure?.let { throw it }
        return appointments.first()
    }
}
