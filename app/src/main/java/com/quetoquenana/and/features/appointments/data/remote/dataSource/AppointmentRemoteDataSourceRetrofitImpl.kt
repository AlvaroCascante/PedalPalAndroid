package com.quetoquenana.and.features.appointments.data.remote.dataSource

import com.quetoquenana.and.features.appointments.data.remote.api.AppointmentApi
import com.quetoquenana.and.features.appointments.data.remote.dto.toDto
import com.quetoquenana.and.features.appointments.data.remote.dto.toDomain
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.AppointmentCreationException
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import javax.inject.Inject
import retrofit2.HttpException

class AppointmentRemoteDataSourceRetrofitImpl @Inject constructor(
    private val api: AppointmentApi
) : AppointmentRemoteDataSource {

    override suspend fun getAppointments(): List<Appointment> {
        return api.getAppointments()
            .data
            .map { it.toDomain() }
    }

    override suspend fun getAppointment(id: String): Appointment {
        return api.getAppointment(id = id).data.toDomain()
    }

    override suspend fun createAppointment(request: CreateAppointmentRequest): Appointment {
        return try {
            api.createAppointment(request.toDto()).data.toDomain()
        } catch (exception: HttpException) {
            throw exception.toAppointmentCreationException()
        }
    }

    private fun HttpException.toAppointmentCreationException(): Throwable {
        val errorBody = response()?.errorBody()?.string().orEmpty()
        val normalizedBody = errorBody.uppercase()
        val serviceUnavailable = code() in setOf(409, 422) &&
            serviceUnavailableCodes.any(normalizedBody::contains)

        return if (serviceUnavailable) {
            AppointmentCreationException.ServiceUnavailable()
        } else {
            this
        }
    }

    private companion object {
        val serviceUnavailableCodes = listOf(
            "SERVICE_UNAVAILABLE",
            "SERVICE_NOT_AVAILABLE",
            "PACKAGE_UNAVAILABLE",
            "PACKAGE_NOT_AVAILABLE",
            "SERVICE_CATALOG_STALE",
            "NO_LONGER_AVAILABLE"
        )
    }
}
