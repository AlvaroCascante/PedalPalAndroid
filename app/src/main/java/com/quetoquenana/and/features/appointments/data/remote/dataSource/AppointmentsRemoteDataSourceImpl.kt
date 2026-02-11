package com.quetoquenana.and.features.appointments.data.remote.dataSource

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.R
import javax.inject.Inject

/**
 * Fake implementation returning deterministic appointments for UI/dev/testing.
 */
class AppointmentsRemoteDataSourceImpl @Inject constructor() : AppointmentsRemoteDataSource {
    override suspend fun getAppointments(): List<com.quetoquenana.and.features.appointments.domain.model.Appointment> {
        // Stable, deterministic fake dataset (date-sorted)
        return listOf(
            _root_ide_package_.com.quetoquenana.and.features.appointments.domain.model.Appointment(
                id = "1",
                dateText = "Mon, Feb 12 · 09:00",
                bikeId = "b1",
                bikeName = "Trek Domane",
                thumbnailRes = R.drawable.mobi_bike_logo
            ),
            _root_ide_package_.com.quetoquenana.and.features.appointments.domain.model.Appointment(
                id = "2",
                dateText = "Fri, Feb 23 · 15:30",
                bikeId = "b2",
                bikeName = "Giant Talon",
                thumbnailRes = null
            ),
            _root_ide_package_.com.quetoquenana.and.features.appointments.domain.model.Appointment(
                id = "3",
                dateText = "Sun, Mar 08 · 09:30",
                bikeId = "b3",
                bikeName = "Specialized Allez",
                thumbnailRes = null
            )
        )
    }
}
