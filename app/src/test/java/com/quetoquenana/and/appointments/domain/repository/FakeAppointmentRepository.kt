package com.quetoquenana.and.appointments.domain.repository

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAppointmentRepository(
    appointments: List<Appointment> = emptyList(),
    private val failure: Throwable? = null
) : AppointmentRepository {

    private val appointmentsFlow = MutableStateFlow(appointments)

    var getAppointmentsCalled = false

    fun emitAppointments(appointments: List<Appointment>) {
        appointmentsFlow.value = appointments
    }

    override suspend fun getAppointments(): List<Appointment> {
        getAppointmentsCalled = true
        failure?.let { throw it }
        return appointmentsFlow.value
    }

    override fun observeAppointments(): Flow<List<Appointment>> {
        return appointmentsFlow
    }

    override suspend fun getAppointmentDetail(id: String): Appointment {
        failure?.let { throw it }
        return appointmentsFlow.value.first { it.id == id }
    }


    override suspend fun createAppointment(request: CreateAppointmentRequest): Appointment {
        failure?.let { throw it }
        return appointmentsFlow.value.first()
    }
}
