package com.quetoquenana.and.bikes.ui

import com.quetoquenana.and.bikes.domain.repository.FakeBikeRepository
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus
import com.quetoquenana.and.features.bikes.domain.usecase.CreateBikeUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaBikesUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaConnectionStatusUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaConnectUrlUseCase
import com.quetoquenana.and.features.bikes.ui.AddBikeViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddBikeViewModelTest {

    @Test
    fun `saveBike with valid data emits NavigateBikes and uses trimmed request`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val fakeRepository = FakeBikeRepository()
            val viewModel = viewModel(fakeRepository)

            viewModel.onNameChanged("  Trek Domane  ")
            viewModel.onTypeChanged(BikeType.ROAD)
            viewModel.onBrandChanged(" Trek ")
            viewModel.onModelChanged(" AL 2 ")
            viewModel.onYearChanged("2024")
            viewModel.onSerialNumberChanged(" SN-1 ")
            viewModel.onNotesChanged(" Weekend bike ")
            viewModel.onOdometerChanged(" 1234 km ")
            viewModel.onIsPublicChanged(true)

            val deferred = CompletableDeferred<AddBikeViewModel.AddBikeEvent>()
            val job = launch {
                viewModel.events.collect {
                    if (!deferred.isCompleted) deferred.complete(it)
                }
            }

            viewModel.saveBike()
            advanceUntilIdle()

            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertNotNull(event)
            assertTrue(event is AddBikeViewModel.AddBikeEvent.NavigateBikes)
            assertEquals("Trek Domane", fakeRepository.lastCreateRequest?.name)
            assertEquals(BikeType.ROAD.name, fakeRepository.lastCreateRequest?.type)
            assertEquals("Trek", fakeRepository.lastCreateRequest?.brand)
            assertEquals("AL 2", fakeRepository.lastCreateRequest?.model)
            assertEquals(2024, fakeRepository.lastCreateRequest?.year)
            assertEquals("SN-1", fakeRepository.lastCreateRequest?.serialNumber)
            assertEquals("Weekend bike", fakeRepository.lastCreateRequest?.notes)
            assertEquals(1234, fakeRepository.lastCreateRequest?.odometerKm)
            assertEquals(null, fakeRepository.lastCreateRequest?.usageTimeMinutes)
            assertEquals(null, fakeRepository.lastCreateRequest?.externalGearId)
            assertEquals(null, fakeRepository.lastCreateRequest?.externalSyncProvider)
            assertEquals(true, fakeRepository.lastCreateRequest?.isPublic)
            assertEquals(false, fakeRepository.lastCreateRequest?.isExternalSync)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `saveBike without name emits validation error`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val viewModel = viewModel(FakeBikeRepository())
            viewModel.onTypeChanged(BikeType.ROAD)

            val deferred = CompletableDeferred<AddBikeViewModel.AddBikeEvent>()
            val job = launch {
                viewModel.events.collect {
                    if (!deferred.isCompleted) deferred.complete(it)
                }
            }

            viewModel.saveBike()
            advanceUntilIdle()

            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertTrue(event is AddBikeViewModel.AddBikeEvent.ShowError)
            assertEquals(
                "Bike name is required",
                (event as AddBikeViewModel.AddBikeEvent.ShowError).message
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `saveBike without type emits validation error`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val viewModel = viewModel(FakeBikeRepository())
            viewModel.onNameChanged("Trek Domane")

            val deferred = CompletableDeferred<AddBikeViewModel.AddBikeEvent>()
            val job = launch {
                viewModel.events.collect {
                    if (!deferred.isCompleted) deferred.complete(it)
                }
            }

            viewModel.saveBike()
            advanceUntilIdle()

            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertTrue(event is AddBikeViewModel.AddBikeEvent.ShowError)
            assertEquals(
                "Bike type is required",
                (event as AddBikeViewModel.AddBikeEvent.ShowError).message
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onAppResumedAfterStravaAuth auto-fills single imported bike after connected fallback`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val fakeRepository = FakeBikeRepository(
                stravaConnectionStatus = StravaConnectionStatus(
                    connected = true,
                    status = "CONNECTED",
                    athleteId = 123L,
                    scope = "read"
                ),
                stravaBikes = listOf(
                    StravaBike(
                        id = "b17558979",
                        name = "Pilsen",
                        nickname = "Pilsen",
                        primary = false,
                        retired = false,
                        distance = 4321.0
                    )
                ),
                stravaBikeFailuresByCall = listOf(IllegalStateException("Strava callback pending"))
            )
            val viewModel = viewModel(fakeRepository)

            viewModel.connectToStrava()
            advanceUntilIdle()

            viewModel.onAppResumedAfterStravaAuth()
            advanceUntilIdle()

            assertEquals("Pilsen", viewModel.uiState.value.name)
            assertEquals("4321", viewModel.uiState.value.odometerKm)
            assertEquals("b17558979", viewModel.uiState.value.importedStravaBikeId)
            assertEquals(2, fakeRepository.getStravaBikesCallCount)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `saveBike after Strava import includes sync metadata`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val fakeRepository = FakeBikeRepository(
                stravaBikes = listOf(
                    StravaBike(
                        id = "b17558979",
                        name = "Pilsen",
                        nickname = "Pilsen",
                        primary = false,
                        retired = false,
                        distance = 987.0
                    )
                )
            )
            val viewModel = viewModel(fakeRepository)

            viewModel.connectToStrava()
            advanceUntilIdle()
            viewModel.onAppResumedAfterStravaAuth()
            advanceUntilIdle()
            viewModel.onTypeChanged(BikeType.GRAVEL)

            val deferred = CompletableDeferred<AddBikeViewModel.AddBikeEvent>()
            val job = launch {
                viewModel.events.collect {
                    if (!deferred.isCompleted) deferred.complete(it)
                }
            }

            viewModel.saveBike()
            advanceUntilIdle()

            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertTrue(event is AddBikeViewModel.AddBikeEvent.NavigateBikes)
            assertEquals("Pilsen", fakeRepository.lastCreateRequest?.name)
            assertEquals(987, fakeRepository.lastCreateRequest?.odometerKm)
            assertEquals("b17558979", fakeRepository.lastCreateRequest?.externalGearId)
            assertEquals("STRAVA", fakeRepository.lastCreateRequest?.externalSyncProvider)
            assertEquals(true, fakeRepository.lastCreateRequest?.isExternalSync)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun viewModel(repository: FakeBikeRepository): AddBikeViewModel {
        return AddBikeViewModel(
            createBikeUseCase = CreateBikeUseCase(repository),
            getStravaConnectUrlUseCase = GetStravaConnectUrlUseCase(repository),
            getStravaConnectionStatusUseCase = GetStravaConnectionStatusUseCase(repository),
            getStravaBikesUseCase = GetStravaBikesUseCase(repository)
        )
    }
}

