package com.quetoquenana.and.features.appointments.data.local.datasource

import com.quetoquenana.and.features.appointments.data.local.entity.AppointmentEntity
import com.quetoquenana.and.features.appointments.data.local.entity.AppointmentServiceEntity
import kotlinx.coroutines.flow.Flow

interface AppointmentLocalDataSource {
    suspend fun getAppointments(): List<AppointmentEntity>
    fun observeAppointments(): Flow<List<AppointmentEntity>>
    suspend fun getAppointmentById(id: String): AppointmentEntity?
    suspend fun getServices(appointmentId: String): List<AppointmentServiceEntity>
    suspend fun saveAppointments(
        appointments: List<AppointmentEntity>,
        services: List<AppointmentServiceEntity>
    )
    suspend fun saveAppointment(
        appointment: AppointmentEntity,
        services: List<AppointmentServiceEntity>
    )
    suspend fun clearAppointments()
}
