package com.quetoquenana.and.features.appointments.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.quetoquenana.and.features.appointments.data.local.entity.AppointmentEntity
import com.quetoquenana.and.features.appointments.data.local.entity.AppointmentServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY scheduledAt ASC")
    suspend fun getAppointments(): List<AppointmentEntity>

    @Query("SELECT * FROM appointments ORDER BY scheduledAt ASC")
    fun observeAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointment_services WHERE appointmentId = :appointmentId")
    suspend fun getServices(appointmentId: String): List<AppointmentServiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAppointments(appointments: List<AppointmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertServices(services: List<AppointmentServiceEntity>)

    @Query("DELETE FROM appointment_services WHERE appointmentId = :appointmentId")
    suspend fun clearServicesForAppointment(appointmentId: String)

    @Transaction
    suspend fun upsertAppointment(
        appointment: AppointmentEntity,
        services: List<AppointmentServiceEntity>
    ) {
        upsertAppointments(listOf(appointment))
        clearServicesForAppointment(appointment.id)
        upsertServices(services)
    }

    @Transaction
    suspend fun upsertAppointmentsWithServices(
        appointments: List<AppointmentEntity>,
        services: List<AppointmentServiceEntity>
    ) {
        upsertAppointments(appointments)
        upsertServices(services)
    }
}
