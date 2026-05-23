package com.quetoquenana.and.features.appointments.domain.usecase

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(): List<Appointment> = repository.getAppointments()

    fun observeAppointments(): Flow<List<Appointment>> = repository.observeAppointments()
}
