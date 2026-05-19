package com.quetoquenana.and.appointments.ui

import androidx.lifecycle.SavedStateHandle
import com.quetoquenana.and.appointments.domain.repository.FakeAppointmentsRepository
import com.quetoquenana.and.features.appointments.AppointmentDetailUiState
import com.quetoquenana.and.features.appointments.AppointmentDetailViewModel
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.usecase.GetAppointmentDetailUseCase
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import com.quetoquenana.and.features.stores.domain.repository.StoreRepository
import com.quetoquenana.and.features.stores.domain.usecase.GetStoresUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppointmentDetailViewModelTest {

    @Test
    fun `load resolves location name from stores`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentsRepository(
                appointments = listOf(
                    appointment(
                        id = "appointment-1",
                        storeLocationId = "location-1"
                    )
                )
            )
            val storeRepository = FakeStoreRepository(
                cachedStores = listOf(storeWithLocation(id = "location-1", name = "San Jose Workshop"))
            )

            val viewModel = AppointmentDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("id" to "appointment-1")),
                getAppointmentDetail = GetAppointmentDetailUseCase(appointmentsRepository),
                getStores = GetStoresUseCase(storeRepository)
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value as AppointmentDetailUiState.Content
            assertEquals("San Jose Workshop", state.appointment.storeLocationName)
            assertEquals("CRC", state.appointment.currency)
            assertEquals(listOf(false), storeRepository.refreshRequests)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `load refreshes stores when cached location is missing`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentsRepository(
                appointments = listOf(
                    appointment(
                        id = "appointment-2",
                        storeLocationId = "location-2"
                    )
                )
            )
            val storeRepository = FakeStoreRepository(
                cachedStores = listOf(storeWithLocation(id = "location-1", name = "San Jose Workshop")),
                refreshedStores = listOf(storeWithLocation(id = "location-2", name = "Escazu Workshop"))
            )

            val viewModel = AppointmentDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("id" to "appointment-2")),
                getAppointmentDetail = GetAppointmentDetailUseCase(appointmentsRepository),
                getStores = GetStoresUseCase(storeRepository)
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value as AppointmentDetailUiState.Content
            assertEquals("Escazu Workshop", state.appointment.storeLocationName)
            assertEquals("CRC", state.appointment.currency)
            assertEquals(listOf(false, true), storeRepository.refreshRequests)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `load keeps appointment content when stores lookup fails`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentsRepository(
                appointments = listOf(
                    appointment(
                        id = "appointment-3",
                        storeLocationId = "location-3"
                    )
                )
            )
            val storeRepository = object : StoreRepository {
                override suspend fun getStores(refresh: Boolean): List<Store> {
                    throw IllegalStateException("Stores unavailable")
                }
            }

            val viewModel = AppointmentDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("id" to "appointment-3")),
                getAppointmentDetail = GetAppointmentDetailUseCase(appointmentsRepository),
                getStores = GetStoresUseCase(storeRepository)
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is AppointmentDetailUiState.Content)
            state as AppointmentDetailUiState.Content
            assertEquals(null, state.appointment.storeLocationName)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun appointment(
        id: String,
        storeLocationId: String
    ): Appointment {
        return Appointment(
            id = id,
            dateText = "2026-05-22",
            bikeId = "bike-1",
            storeLocationId = storeLocationId,
            scheduledAt = "2026-05-22T09:30:00Z",
            status = "CONFIRMED"
        )
    }

    private fun storeWithLocation(id: String, name: String): Store {
        return Store(
            id = "store-1",
            name = "PedalPal",
            locations = listOf(
                StoreLocation(
                    id = id,
                    storeId = "store-1",
                    name = name,
                    storePrefix = null,
                    website = null,
                    address = null,
                    latitude = null,
                    longitude = null,
                    phone = null,
                    currency = "CRC",
                    timezone = null,
                    status = "ACTIVE"
                )
            )
        )
    }

    private class FakeStoreRepository(
        private val cachedStores: List<Store> = emptyList(),
        private val refreshedStores: List<Store> = cachedStores
    ) : StoreRepository {
        val refreshRequests = mutableListOf<Boolean>()

        override suspend fun getStores(refresh: Boolean): List<Store> {
            refreshRequests += refresh
            return if (refresh) refreshedStores else cachedStores
        }
    }
}

