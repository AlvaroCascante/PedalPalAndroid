package com.quetoquenana.and.features.appointments.domain.usecase

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository
import javax.inject.Inject

class GetAppointmentsUseCase @Inject constructor(
    private val repository: com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository
) {
    suspend operator fun invoke(): List<com.quetoquenana.and.features.appointments.domain.model.Appointment> = repository.getAppointments()
}
