package com.quetoquenana.and.features.appointments.data.repository

import com.quetoquenana.and.features.appointments.data.local.datasource.AppointmentLocalDataSource
import com.quetoquenana.and.features.appointments.data.local.entity.toDomain
import com.quetoquenana.and.features.appointments.data.local.entity.toEntity
import com.quetoquenana.and.features.appointments.data.remote.dataSource.AppointmentsRemoteDataSource
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class AppointmentsRepositoryImpl @Inject constructor(
    private val local: AppointmentLocalDataSource,
    private val remote: AppointmentsRemoteDataSource
) : AppointmentsRepository {

    override suspend fun getAppointments(): List<Appointment> {
        return runCatching {
            val appointments = remote.getAppointments()
            val now = System.currentTimeMillis()
            local.saveAppointments(
                appointments = appointments.map { it.toEntity(currentTimeMillis = now) },
                services = appointments.flatMap { appointment ->
                    appointment.requestedServices.map { it.toEntity(appointmentId = appointment.id) }
                }
            )
            appointments
        }.getOrElse {
            local.getAppointments().map { entity ->
                entity.toDomain(services = local.getServices(appointmentId = entity.id))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeAppointments(): Flow<List<Appointment>> =
        local.observeAppointments().flatMapLatest { entities ->
            flow {
                val appointments = entities.map { entity ->
                    entity.toDomain(services = local.getServices(appointmentId = entity.id))
                }
                emit(value = appointments)
            }
        }

    override suspend fun createAppointment(request: CreateAppointmentRequest): Appointment {
        val appointment = remote.createAppointment(request)
        local.saveAppointment(
            appointment = appointment.toEntity(currentTimeMillis = System.currentTimeMillis()),
            services = appointment.requestedServices.map { it.toEntity(appointmentId = appointment.id) }
        )
        return appointment
    }
}
