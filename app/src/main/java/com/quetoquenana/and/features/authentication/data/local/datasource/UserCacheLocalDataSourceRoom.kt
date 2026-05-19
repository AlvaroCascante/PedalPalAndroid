package com.quetoquenana.and.features.authentication.data.local.datasource

import com.quetoquenana.and.features.appointments.data.local.datasource.AppointmentLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeComponentLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeLocalDataSource
import javax.inject.Inject

class UserCacheLocalDataSourceRoom @Inject constructor(
    private val appointmentLocalDataSource: AppointmentLocalDataSource,
    private val bikeLocalDataSource: BikeLocalDataSource,
    private val bikeComponentLocalDataSource: BikeComponentLocalDataSource,
) : UserCacheLocalDataSource {

    override suspend fun clearUserRideData() {
        appointmentLocalDataSource.clearAppointments()
        bikeComponentLocalDataSource.clearComponents()
        bikeLocalDataSource.clearBikes()
    }
}

