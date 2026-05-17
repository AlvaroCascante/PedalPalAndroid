package com.quetoquenana.and.features.appointments.data.repository

import com.quetoquenana.and.features.appointments.data.local.datasource.AppointmentLocalDataSource
import com.quetoquenana.and.features.appointments.data.local.entity.toDomain
import com.quetoquenana.and.features.appointments.data.local.entity.toEntity
import com.quetoquenana.and.features.appointments.data.remote.dataSource.AppointmentsRemoteDataSource
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeLocalDataSource
import com.quetoquenana.and.features.stores.data.local.datasource.StoreLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class AppointmentsRepositoryImpl @Inject constructor(
    private val local: AppointmentLocalDataSource,
    private val remote: AppointmentsRemoteDataSource,
    private val storeLocal: StoreLocalDataSource,
    private val bikeLocal: BikeLocalDataSource
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
            appointments.map { it.withLocalNames() }
        }.getOrElse {
            local.getAppointments().map { entity ->
                entity.toDomain(services = local.getServices(appointmentId = entity.id))
                    .withLocalNames()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeAppointments(): Flow<List<Appointment>> =
        local.observeAppointments().flatMapLatest { entities ->
            flow {
                val appointments = entities.map { entity ->
                    entity.toDomain(services = local.getServices(appointmentId = entity.id))
                        .withLocalNames()
                }
                emit(value = appointments)
            }
        }

    override suspend fun getAppointmentDetail(id: String): Appointment {
        // Show cached data immediately if available; always fetch remote to refresh.
        val cached = local.getAppointmentById(id)?.let { entity ->
            entity.toDomain(services = local.getServices(appointmentId = entity.id))
                .withLocalNames()
        }

        return runCatching {
            val fresh = remote.getAppointment(id = id)
            val now = System.currentTimeMillis()
            local.saveAppointment(
                appointment = fresh.toEntity(currentTimeMillis = now),
                services = fresh.requestedServices.map { it.toEntity(appointmentId = fresh.id) }
            )
            fresh.withLocalNames()
        }.getOrElse { cached ?: throw it }
    }

    override suspend fun createAppointment(request: CreateAppointmentRequest): Appointment {
        val appointment = remote.createAppointment(request)
        local.saveAppointment(
            appointment = appointment.toEntity(currentTimeMillis = System.currentTimeMillis()),
            services = appointment.requestedServices.map { it.toEntity(appointmentId = appointment.id) }
        )
        return appointment.withLocalNames()
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /** Enriches an [Appointment] with store location name, currency, and bike name
     *  by querying the local cache. Returns the same instance if nothing is found
     *  (first-launch cold cache). */
    private suspend fun Appointment.withLocalNames(): Appointment {
        val location = storeLocationId?.let { storeLocal.getLocationById(it) }
        val bike = bikeLocal.getBikeById(bikeId)
        return copy(
            storeLocationName = location?.name ?: storeLocationName,
            currency = location?.currency ?: currency,
            bikeName = bike?.name ?: bikeName
        )
    }
}
