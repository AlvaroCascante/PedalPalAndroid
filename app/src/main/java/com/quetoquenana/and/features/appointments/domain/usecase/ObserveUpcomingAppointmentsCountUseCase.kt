package com.quetoquenana.and.features.appointments.domain.usecase

import com.quetoquenana.and.features.appointments.domain.model.isUpcoming
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveUpcomingAppointmentsCountUseCase @Inject constructor(
    private val repository: AppointmentsRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.observeAppointments()
            .map { appointments ->
                appointments.count { appointment -> appointment.isUpcoming() }
            }
    }
}

