package com.quetoquenana.and.features.appointments.domain.usecase

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentRepository
import javax.inject.Inject

class GetAppointmentDetailUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(id: String): Appointment = repository.getAppointmentDetail(id)
}

