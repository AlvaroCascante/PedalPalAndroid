package com.quetoquenana.and.features.appointments.domain.model

sealed class AppointmentCreationException(message: String) : Exception(message) {
    class ServiceUnavailable(
        message: String = "One or more selected services are no longer available. Refresh services and choose again."
    ) : AppointmentCreationException(message)
}
