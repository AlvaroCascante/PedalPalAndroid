package com.quetoquenana.and.pedalpal.feature.appointments.domain.usecase

import com.quetoquenana.and.pedalpal.feature.appointments.domain.model.Appointment
import com.quetoquenana.and.pedalpal.feature.appointments.domain.repository.AppointmentsRepository
import javax.inject.Inject

class GetAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentsRepository
) {
    suspend operator fun invoke(): List<Appointment> = repository.getAppointments()
}
