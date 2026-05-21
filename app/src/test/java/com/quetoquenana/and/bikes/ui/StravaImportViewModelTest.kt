package com.quetoquenana.and.bikes.ui

import com.quetoquenana.and.bikes.domain.repository.FakeBikeRepository
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaBikesUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaConnectionStatusUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaConnectUrlUseCase
import com.quetoquenana.and.features.bikes.ui.StravaImportViewModel
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
class StravaImportViewModelTest {

    @Test
    fun `connectToStrava emits OpenBrowser`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val repo = FakeBikeRepository()
            val viewModel = StravaImportViewModel(
                getStravaConnectUrlUseCase = GetStravaConnectUrlUseCase(repo),
                getStravaConnectionStatusUseCase = GetStravaConnectionStatusUseCase(repo),
                getStravaBikesUseCase = GetStravaBikesUseCase(repo)
            )

            val deferred = CompletableDeferred<StravaImportViewModel.StravaImportEvent>()
            val job = launch {
                viewModel.events.collect {
                    if (!deferred.isCompleted) deferred.complete(it)
                }
            }

            viewModel.connectToStrava()
            advanceUntilIdle()

            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertNotNull(event)
            assertTrue(event is StravaImportViewModel.StravaImportEvent.OpenBrowser)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onAppResumedAfterAuth auto-navigates when exactly one strava bike is available`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val repo = FakeBikeRepository(
                stravaBikes = listOf(
                    StravaBike(
                        id = "s1",
                        name = "Strava Bike",
                        nickname = "Fast one",
                        primary = true,
                        retired = false,
                        distance = 1234.0
                    )
                )
            )
            val viewModel = StravaImportViewModel(
                getStravaConnectUrlUseCase = GetStravaConnectUrlUseCase(repo),
                getStravaConnectionStatusUseCase = GetStravaConnectionStatusUseCase(repo),
                getStravaBikesUseCase = GetStravaBikesUseCase(repo)
            )

            val deferred = CompletableDeferred<StravaImportViewModel.StravaImportEvent>()
            val job = launch {
                viewModel.events.collect {
                    if (it !is StravaImportViewModel.StravaImportEvent.OpenBrowser && !deferred.isCompleted) {
                        deferred.complete(it)
                    }
                }
            }

            viewModel.connectToStrava()
            advanceUntilIdle()

            viewModel.onAppResumedAfterAuth()
            advanceUntilIdle()

            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertTrue(event is StravaImportViewModel.StravaImportEvent.NavigateToCreateBike)
            assertEquals(
                "Strava Bike",
                (event as StravaImportViewModel.StravaImportEvent.NavigateToCreateBike).bike.name
            )
            assertTrue(viewModel.uiState.value.bikes.isEmpty())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onAppResumedAfterAuth retries bikes after connected status fallback`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val repo = FakeBikeRepository(
                stravaConnectionStatus = com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus(
                    connected = true,
                    status = "CONNECTED",
                    athleteId = 10L,
                    scope = "read"
                ),
                stravaBikes = listOf(
                    StravaBike(
                        id = "s1",
                        name = "Strava Bike",
                        nickname = "Fast one",
                        primary = true,
                        retired = false,
                        distance = 1234.0
                    )
                ),
                stravaBikeFailuresByCall = listOf(IllegalStateException("Pending callback"))
            )
            val viewModel = StravaImportViewModel(
                getStravaConnectUrlUseCase = GetStravaConnectUrlUseCase(repo),
                getStravaConnectionStatusUseCase = GetStravaConnectionStatusUseCase(repo),
                getStravaBikesUseCase = GetStravaBikesUseCase(repo)
            )

            viewModel.connectToStrava()
            advanceUntilIdle()

            viewModel.onAppResumedAfterAuth()
            advanceUntilIdle()

            assertEquals(2, repo.getStravaBikesCallCount)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onAppResumedAfterAuth keeps bikes in state when multiple strava bikes are returned`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val repo = FakeBikeRepository(
                stravaBikes = listOf(
                    StravaBike(
                        id = "s1",
                        name = "Road Bike",
                        nickname = "Fast one",
                        primary = true,
                        retired = false,
                        distance = 1234.0
                    ),
                    StravaBike(
                        id = "s2",
                        name = "Gravel Bike",
                        nickname = "Adventure",
                        primary = false,
                        retired = false,
                        distance = 567.0
                    )
                )
            )
            val viewModel = StravaImportViewModel(
                getStravaConnectUrlUseCase = GetStravaConnectUrlUseCase(repo),
                getStravaConnectionStatusUseCase = GetStravaConnectionStatusUseCase(repo),
                getStravaBikesUseCase = GetStravaBikesUseCase(repo)
            )

            viewModel.connectToStrava()
            advanceUntilIdle()
            viewModel.onAppResumedAfterAuth()
            advanceUntilIdle()

            assertEquals(2, viewModel.uiState.value.bikes.size)
            assertEquals("Road Bike", viewModel.uiState.value.bikes.first().name)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

