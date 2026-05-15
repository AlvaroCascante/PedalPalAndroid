package com.quetoquenana.and.appointments.ui

import com.quetoquenana.and.features.appointments.AddAppointmentViewModel
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.AppointmentCreationException
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository
import com.quetoquenana.and.features.appointments.domain.usecase.CreateAppointmentUseCase
import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import com.quetoquenana.and.features.services.domain.model.ServiceProduct
import com.quetoquenana.and.features.services.domain.repository.ServiceCatalogRepository
import com.quetoquenana.and.features.services.domain.usecase.GetServiceCatalogUseCase
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import com.quetoquenana.and.features.stores.domain.repository.StoreRepository
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikesUseCase
import com.quetoquenana.and.features.stores.domain.usecase.GetStoresUseCase
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class AddAppointmentViewModelTest {

    @Test
    fun `selecting store location fetches services for that location only`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val serviceRepository = FakeServiceCatalogRepository()
            val viewModel = AddAppointmentViewModel(
                getStoresUseCase = GetStoresUseCase(FakeStoreRepository()),
                getServiceCatalogUseCase = GetServiceCatalogUseCase(serviceRepository),
                createAppointmentUseCase = CreateAppointmentUseCase(FakeAppointmentsRepository()),
                getBikesUseCase = GetBikesUseCase(FakeBikeRepository())
            )

            advanceUntilIdle()

            assertTrue(serviceRepository.requestedStoreLocationIds.isEmpty())

            viewModel.onStoreSelected("store-1")
            advanceUntilIdle()

            assertTrue(serviceRepository.requestedStoreLocationIds.isEmpty())

            viewModel.onLocationSelected("location-2")
            advanceUntilIdle()

            assertEquals(listOf("location-2"), serviceRepository.requestedStoreLocationIds)
            assertEquals(listOf("prod-location-2"), viewModel.uiState.value.serviceCatalog.products.map { it.id })
            assertEquals(200L, viewModel.uiState.value.serviceCatalog.lastUpdated)
            assertEquals(200L, viewModel.uiState.value.catalogLastUpdated)
            assertFalse(viewModel.uiState.value.isUsingCachedCatalog)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `single store and location are auto selected and fetch services`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val serviceRepository = FakeServiceCatalogRepository()
            val viewModel = AddAppointmentViewModel(
                getStoresUseCase = GetStoresUseCase(
                    FakeStoreRepository(
                        stores = listOf(
                            Store(
                                id = "store-only",
                                name = "PedalPal",
                                locations = listOf(location(id = "location-only", storeId = "store-only"))
                            )
                        )
                    )
                ),
                getServiceCatalogUseCase = GetServiceCatalogUseCase(serviceRepository),
                createAppointmentUseCase = CreateAppointmentUseCase(FakeAppointmentsRepository()),
                getBikesUseCase = GetBikesUseCase(FakeBikeRepository())
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals("store-only", state.selectedStoreId)
            assertEquals("location-only", state.selectedLocationId)
            assertEquals(listOf("location-only"), serviceRepository.requestedStoreLocationIds)
            assertEquals(listOf("prod-location-only"), state.serviceCatalog.products.map { it.id })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `remote catalog fallback exposes cached freshness and retry state`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val serviceRepository = FakeServiceCatalogRepository(
                catalog = ServiceCatalog(
                    products = listOf(
                        ServiceProduct(
                            id = "cached-prod",
                            name = "Cached brake check",
                            description = null,
                            price = "10.00",
                            status = "ACTIVE"
                        )
                    ),
                    lastUpdated = 123L,
                    isFromCache = true,
                    fetchErrorMessage = "Unable to refresh services"
                )
            )
            val viewModel = AddAppointmentViewModel(
                getStoresUseCase = GetStoresUseCase(FakeStoreRepository()),
                getServiceCatalogUseCase = GetServiceCatalogUseCase(serviceRepository),
                createAppointmentUseCase = CreateAppointmentUseCase(FakeAppointmentsRepository()),
                getBikesUseCase = GetBikesUseCase(FakeBikeRepository())
            )

            advanceUntilIdle()
            viewModel.onStoreSelected("store-1")
            viewModel.onLocationSelected("location-1")
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state.isUsingCachedCatalog)
            assertEquals(123L, state.catalogLastUpdated)
            assertEquals("Unable to refresh services", state.catalogFetchErrorMessage)
            assertEquals(listOf("cached-prod"), state.serviceCatalog.products.map { it.id })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `submitAppointment surfaces service unavailable domain error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val viewModel = AddAppointmentViewModel(
                getStoresUseCase = GetStoresUseCase(FakeStoreRepository()),
                getServiceCatalogUseCase = GetServiceCatalogUseCase(FakeServiceCatalogRepository()),
                createAppointmentUseCase = CreateAppointmentUseCase(
                    FakeAppointmentsRepository(failure = AppointmentCreationException.ServiceUnavailable())
                ),
                getBikesUseCase = GetBikesUseCase(FakeBikeRepository())
            )

            viewModel.submitAppointment(
                CreateAppointmentRequest(
                    bikeId = "bike-1",
                    storeLocationId = "location-1",
                    scheduledAt = "2026-05-01T10:00:00Z",
                    notes = null,
                    requestedServices = emptyList()
                )
            )
            advanceUntilIdle()

            assertEquals(
                "One or more selected services are no longer available. Refresh services and choose again.",
                viewModel.uiState.value.submitErrorMessage
            )
            assertFalse(viewModel.uiState.value.isSubmitting)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `createAppointment submits selected location and services`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentsRepository()
            val viewModel = AddAppointmentViewModel(
                getStoresUseCase = GetStoresUseCase(FakeStoreRepository()),
                getServiceCatalogUseCase = GetServiceCatalogUseCase(FakeServiceCatalogRepository()),
                createAppointmentUseCase = CreateAppointmentUseCase(appointmentsRepository),
                getBikesUseCase = GetBikesUseCase(FakeBikeRepository())
            )

            advanceUntilIdle()
            viewModel.onStoreSelected("store-1")
            viewModel.onLocationSelected("location-1")
            val selectedDateMillis = LocalDate.of(2099, 1, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
            viewModel.onScheduledDateSelected(selectedDateMillis)
            advanceUntilIdle()
            viewModel.onProductToggled("prod-location-1")
            viewModel.createAppointment()
            advanceUntilIdle()

            val request = appointmentsRepository.createdRequest
            assertEquals("location-1", request?.storeLocationId)
            assertEquals(Instant.ofEpochMilli(selectedDateMillis).toString(), request?.scheduledAt)
            assertEquals(listOf("prod-location-1"), request?.requestedServices?.map { it.serviceId })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `createAppointment rejects past selected date`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentsRepository()
            val viewModel = AddAppointmentViewModel(
                getStoresUseCase = GetStoresUseCase(FakeStoreRepository()),
                getServiceCatalogUseCase = GetServiceCatalogUseCase(FakeServiceCatalogRepository()),
                createAppointmentUseCase = CreateAppointmentUseCase(appointmentsRepository),
                getBikesUseCase = GetBikesUseCase(FakeBikeRepository())
            )

            advanceUntilIdle()
            viewModel.onStoreSelected("store-1")
            viewModel.onLocationSelected("location-1")
            viewModel.onProductToggled("prod-location-1")

            val yesterdayMillis = LocalDate.now(ZoneOffset.UTC)
                .minusDays(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
            viewModel.onScheduledDateSelected(yesterdayMillis)
            advanceUntilIdle()

            viewModel.createAppointment()
            advanceUntilIdle()

            assertEquals(
                "Select today or a future date for the appointment.",
                viewModel.uiState.value.submitErrorMessage
            )
            assertEquals(null, appointmentsRepository.createdRequest)
        } finally {
            Dispatchers.resetMain()
        }
    }


    private class FakeStoreRepository(
        private val stores: List<Store> = listOf(
            Store(
                id = "store-1",
                name = "PedalPal",
                locations = listOf(
                    location(id = "location-1", storeId = "store-1"),
                    location(id = "location-2", storeId = "store-1")
                )
            )
        )
    ) : StoreRepository {
        override suspend fun getStores(refresh: Boolean): List<Store> {
            return stores
        }
    }

    private class FakeServiceCatalogRepository(
        private val catalog: ServiceCatalog? = null
    ) : ServiceCatalogRepository {
        val requestedStoreLocationIds = mutableListOf<String>()

        override suspend fun getCatalog(storeLocationId: String, refresh: Boolean): ServiceCatalog {
            requestedStoreLocationIds += storeLocationId
            catalog?.let { return it }
            return ServiceCatalog(
                products = listOf(
                    ServiceProduct(
                        id = "prod-$storeLocationId",
                        name = "Brake check",
                        description = null,
                        price = "10.00",
                        status = "ACTIVE"
                    )
                ),
                lastUpdated = 200L
            )
        }
    }

    private class FakeAppointmentsRepository(
        private val failure: Throwable? = null
    ) : AppointmentsRepository {
        var createdRequest: CreateAppointmentRequest? = null

        override suspend fun getAppointments(): List<Appointment> = emptyList()

        override fun observeAppointments(): kotlinx.coroutines.flow.Flow<List<Appointment>> =
            kotlinx.coroutines.flow.flowOf(emptyList())

        override suspend fun createAppointment(request: CreateAppointmentRequest): Appointment {
            failure?.let { throw it }
            createdRequest = request
            return Appointment(
                id = "appointment-1",
                dateText = request.scheduledAt,
                bikeId = request.bikeId,
                storeLocationId = request.storeLocationId,
                scheduledAt = request.scheduledAt,
                status = "REQUESTED"
            )
        }
    }

    private class FakeBikeRepository : com.quetoquenana.and.features.bikes.domain.repository.BikeRepository {
        private val bikes = listOf(
            com.quetoquenana.and.features.bikes.domain.model.Bike(
                id = "bike-1",
                name = "Road Bike",
                type = "ROAD",
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
        )

        override suspend fun getBikeComponentTypes(): List<com.quetoquenana.and.features.bikes.domain.model.BikeComponentType> = emptyList()

        override fun observeBikes(): kotlinx.coroutines.flow.Flow<List<com.quetoquenana.and.features.bikes.domain.model.Bike>> =
            kotlinx.coroutines.flow.flowOf(bikes)

        override suspend fun getBikes(refresh: Boolean): List<com.quetoquenana.and.features.bikes.domain.model.Bike> = bikes

        override suspend fun getBike(id: String): com.quetoquenana.and.features.bikes.domain.model.Bike {
            throw NoSuchElementException("Bike not found in fake repository")
        }

        override suspend fun getBikeHistory(id: String): List<com.quetoquenana.and.features.bikes.domain.model.BikeHistory> = emptyList()

        override suspend fun createBike(request: com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest): com.quetoquenana.and.features.bikes.domain.model.Bike {
            throw UnsupportedOperationException("Not required for this test")
        }

        override suspend fun addBikeComponent(
            bikeId: String,
            request: com.quetoquenana.and.features.bikes.domain.model.AddBikeComponentRequest
        ): com.quetoquenana.and.features.bikes.domain.model.BikeComponent {
            throw UnsupportedOperationException("Not required for this test")
        }

        override suspend fun getStravaConnectUrl(): com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl {
            throw UnsupportedOperationException("Not required for this test")
        }

        override suspend fun getStravaBikes(): List<com.quetoquenana.and.features.bikes.domain.model.StravaBike> = emptyList()
    }

    private companion object {
        fun location(id: String, storeId: String): StoreLocation {
            return StoreLocation(
                id = id,
                storeId = storeId,
                name = id,
                storePrefix = null,
                website = null,
                address = null,
                latitude = null,
                longitude = null,
                phone = null,
                timezone = null,
                status = "ACTIVE"
            )
        }
    }
}
