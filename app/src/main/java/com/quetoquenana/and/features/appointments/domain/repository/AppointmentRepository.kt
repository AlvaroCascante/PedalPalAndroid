package com.quetoquenana.and.features.appointments.domain.repository

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    suspend fun getAppointments(): List<Appointment>
    fun observeAppointments(): Flow<List<Appointment>>
    suspend fun getAppointmentDetail(id: String): Appointment
    suspend fun createAppointment(request: CreateAppointmentRequest): Appointment
}
