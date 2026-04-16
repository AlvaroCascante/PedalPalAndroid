package com.quetoquenana.and.appointments.domain.repository

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository

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
}
