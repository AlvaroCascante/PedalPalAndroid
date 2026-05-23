package com.quetoquenana.and.core.ui.navigation

import com.quetoquenana.and.appointments.domain.repository.FakeAppointmentRepository
import com.quetoquenana.and.auth.domain.repository.FakeAuthRepository
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.usecase.ObserveUpcomingAppointmentsCountUseCase
import com.quetoquenana.and.features.authentication.domain.usecase.GetUserDisplayNameUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @Test
    fun `appointments badge count includes only globally upcoming appointments`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentRepository(
                appointments = listOf(
                    appointment(
                        id = "future-requested",
                        scheduledAt = "2099-04-20T15:30:00Z",
                        status = "REQUESTED"
                    ),
                    appointment(
                        id = "future-confirmed",
                        scheduledAt = "2099-04-21T15:30:00Z",
                        status = "CONFIRMED"
                    ),
                    appointment(
                        id = "future-cancelled",
                        scheduledAt = "2099-04-22T15:30:00Z",
                        status = "CANCELLED"
                    ),
                    appointment(
                        id = "past-completed",
                        scheduledAt = "2020-01-10T09:00:00Z",
                        status = "COMPLETED"
                    ),
                    appointment(
                        id = "missing-date",
                        scheduledAt = null,
                        status = "REQUESTED"
                    ),
                    appointment(
                        id = "invalid-date",
                        scheduledAt = "invalid-date",
                        status = "REQUESTED"
                    )
                )
            )

            val viewModel = MainViewModel(
                getUserDisplayNameUseCase = GetUserDisplayNameUseCase(
                    authRepository = FakeAuthRepository(currentUserDisplayName = "Ava")
                ),
                observeUpcomingAppointmentsCountUseCase = ObserveUpcomingAppointmentsCountUseCase(
                    repository = appointmentsRepository
                )
            )

            advanceUntilIdle()

            assertEquals(2, viewModel.appointmentsBadgeCount.value)
            assertEquals("Ava", viewModel.userDisplayName.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `appointments badge count updates when appointments flow changes`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentRepository()
            val viewModel = MainViewModel(
                getUserDisplayNameUseCase = GetUserDisplayNameUseCase(
                    authRepository = FakeAuthRepository(currentUserDisplayName = null)
                ),
                observeUpcomingAppointmentsCountUseCase = ObserveUpcomingAppointmentsCountUseCase(
                    repository = appointmentsRepository
                )
            )

            advanceUntilIdle()
            assertEquals(0, viewModel.appointmentsBadgeCount.value)

            appointmentsRepository.emitAppointments(
                listOf(
                    appointment(
                        id = "future-requested",
                        scheduledAt = "2099-04-20T15:30:00Z",
                        status = "REQUESTED"
                    ),
                    appointment(
                        id = "future-confirmed",
                        scheduledAt = "2099-04-21T15:30:00Z",
                        status = "CONFIRMED"
                    ),
                    appointment(
                        id = "past-requested",
                        scheduledAt = "2020-04-21T15:30:00Z",
                        status = "REQUESTED"
                    )
                )
            )

            advanceUntilIdle()
            assertEquals(2, viewModel.appointmentsBadgeCount.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun appointment(
        id: String,
        scheduledAt: String?,
        status: String
    ): Appointment {
        return Appointment(
            id = id,
            dateText = scheduledAt ?: "Unknown date",
            bikeId = "bike-1",
            storeLocationId = "store-1",
            scheduledAt = scheduledAt,
            status = status
        )
    }
}


