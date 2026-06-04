package com.quetoquenana.and.features.appointments.data.local.datasource

import com.quetoquenana.and.features.appointments.data.local.dao.AppointmentDao
import com.quetoquenana.and.features.appointments.data.local.entity.AppointmentEntity
import com.quetoquenana.and.features.appointments.data.local.entity.AppointmentServiceEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AppointmentLocalDataSourceRoom @Inject constructor(
    private val dao: AppointmentDao
) : AppointmentLocalDataSource {
    override suspend fun getAppointments(): List<AppointmentEntity> = dao.getAppointments()
    override fun observeAppointments(): Flow<List<AppointmentEntity>> = dao.observeAppointments()
    override suspend fun getAppointmentById(id: UUID): AppointmentEntity? = dao.getAppointmentById(id)
    override suspend fun getServices(appointmentId: UUID): List<AppointmentServiceEntity> {
        return dao.getServices(appointmentId)
    }

    override suspend fun saveAppointments(
        appointments: List<AppointmentEntity>,
        services: List<AppointmentServiceEntity>
    ) {
        dao.upsertAppointmentsWithServices(appointments, services)
    }

    override suspend fun saveAppointment(
        appointment: AppointmentEntity,
        services: List<AppointmentServiceEntity>
    ) {
        dao.upsertAppointment(appointment, services)
    }

    override suspend fun clearAppointments() {
        dao.clearAll()
    }
}
