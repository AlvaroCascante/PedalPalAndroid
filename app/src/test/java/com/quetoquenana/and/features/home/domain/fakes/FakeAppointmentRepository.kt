package com.quetoquenana.and.features.home.domain.fakes

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class FakeAppointmentRepository(
    private val appointments: List<Appointment> = emptyList()
) : AppointmentRepository {
    override suspend fun getAppointments(): List<Appointment> = appointments
    override fun observeAppointments(): Flow<List<Appointment>> = flowOf(appointments)
    override suspend fun getAppointmentDetail(id: UUID): Appointment =
        appointments.firstOrNull { it.id == id } ?: throw NoSuchElementException("Appointment not found: $id")
    override suspend fun createAppointment(request: CreateAppointmentRequest): Appointment =
        appointments.firstOrNull() ?: throw NoSuchElementException("No appointments available")
}
