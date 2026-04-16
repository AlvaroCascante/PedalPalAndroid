package com.quetoquenana.and.appointments.ui

import com.quetoquenana.and.appointments.domain.repository.FakeAppointmentsRepository
import com.quetoquenana.and.bikes.domain.repository.FakeBikeRepository
import com.quetoquenana.and.features.appointments.AppointmentsViewModel
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.usecase.GetAppointmentsUseCase
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppointmentsViewModelTest {

    @Test
    fun `loadAppointments joins bike names and splits upcoming and past appointments`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val roadBike = bike(id = "bike-road", name = "Road Bike")
            val gravelBike = bike(id = "bike-gravel", name = "Gravel Bike")
            val appointmentsRepository = FakeAppointmentsRepository(
                appointments = listOf(
                    appointment(
                        id = "past-completed",
                        bikeId = roadBike.id,
                        scheduledAt = "2020-01-10T09:00:00Z",
                        status = "COMPLETED"
                    ),
                    appointment(
                        id = "future-confirmed",
                        bikeId = roadBike.id,
                        scheduledAt = "2099-04-20T15:30:00Z",
                        status = "CONFIRMED"
                    ),
                    appointment(
                        id = "future-cancelled",
                        bikeId = gravelBike.id,
                        scheduledAt = "2099-04-21T15:30:00Z",
                        status = "CANCELLED"
                    )
                )
            )

            val viewModel = viewModel(
                appointmentsRepository = appointmentsRepository,
                bikeRepository = FakeBikeRepository(initialBikes = listOf(roadBike, gravelBike))
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(appointmentsRepository.getAppointmentsCalled)
            assertFalse(state.isLoading)
            assertEquals(listOf("Gravel Bike", "Road Bike"), state.bikeFilters.map { it.bikeName })
            assertEquals("Road Bike", state.appointments.first { it.id == "future-confirmed" }.bikeName)
            assertEquals(listOf("future-confirmed"), state.upcomingAppointments.map { it.id })
            assertEquals(listOf("future-cancelled", "past-completed"), state.pastAppointments.map { it.id })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onBikeFilterSelected filters upcoming and past appointments for selected bike`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val roadBike = bike(id = "bike-road", name = "Road Bike")
            val cityBike = bike(id = "bike-city", name = "City Bike")
            val viewModel = viewModel(
                appointmentsRepository = FakeAppointmentsRepository(
                    appointments = listOf(
                        appointment(
                            id = "road-upcoming",
                            bikeId = roadBike.id,
                            scheduledAt = "2099-05-01T10:00:00Z",
                            status = "REQUESTED"
                        ),
                        appointment(
                            id = "road-past",
                            bikeId = roadBike.id,
                            scheduledAt = "2020-05-01T10:00:00Z",
                            status = "COMPLETED"
                        ),
                        appointment(
                            id = "city-upcoming",
                            bikeId = cityBike.id,
                            scheduledAt = "2099-05-02T10:00:00Z",
                            status = "REQUESTED"
                        )
                    )
                ),
                bikeRepository = FakeBikeRepository(initialBikes = listOf(roadBike, cityBike))
            )

            advanceUntilIdle()
            viewModel.onBikeFilterSelected(roadBike.id)

            val state = viewModel.uiState.value
            assertEquals("Road Bike", state.selectedBikeName)
            assertEquals(listOf("road-upcoming"), state.upcomingAppointments.map { it.id })
            assertEquals(listOf("road-past"), state.pastAppointments.map { it.id })

            viewModel.onBikeFilterSelected(null)
            assertEquals(3, viewModel.uiState.value.filteredAppointments.size)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `loadAppointments uses shortened bike id when bike metadata is missing`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val viewModel = viewModel(
                appointmentsRepository = FakeAppointmentsRepository(
                    appointments = listOf(
                        appointment(
                            id = "missing-bike",
                            bikeId = "12345678-aaaa-bbbb-cccc-123456789012",
                            scheduledAt = "2099-06-01T10:00:00Z",
                            status = "REQUESTED"
                        )
                    )
                ),
                bikeRepository = FakeBikeRepository(initialBikes = emptyList())
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals("Bike 12345678", state.bikeFilters.single().bikeName)
            assertEquals("Bike 12345678", state.appointments.single().bikeName)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `loadAppointments failure exposes error message and stops loading`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val viewModel = viewModel(
                appointmentsRepository = FakeAppointmentsRepository(
                    failure = IllegalStateException("network down")
                ),
                bikeRepository = FakeBikeRepository()
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals("network down", state.errorMessage)
            assertTrue(state.appointments.isEmpty())
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun viewModel(
        appointmentsRepository: FakeAppointmentsRepository,
        bikeRepository: FakeBikeRepository
    ): AppointmentsViewModel {
        return AppointmentsViewModel(
            getAppointmentsUseCase = GetAppointmentsUseCase(appointmentsRepository),
            getBikesUseCase = GetBikesUseCase(bikeRepository)
        )
    }

    private fun appointment(
        id: String,
        bikeId: String,
        scheduledAt: String,
        status: String
    ): Appointment {
        return Appointment(
            id = id,
            dateText = scheduledAt,
            bikeId = bikeId,
            storeLocationId = "store-1",
            scheduledAt = scheduledAt,
            status = status
        )
    }

    private fun bike(id: String, name: String): Bike {
        return Bike(
            id = id,
            name = name,
            type = "Road",
            status = "ACTIVE",
            isPublic = false,
            isExternalSync = false,
            brand = null,
            model = null,
            year = null,
            serialNumber = null,
            notes = null,
            odometerKm = 0.0,
            usageTimeMinutes = 0,
            externalGearId = null,
            externalSyncProvider = ""
        )
    }
}
