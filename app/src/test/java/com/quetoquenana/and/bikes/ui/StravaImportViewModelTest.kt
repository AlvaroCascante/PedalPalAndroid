package com.quetoquenana.and.bikes.ui

import com.quetoquenana.and.bikes.domain.repository.FakeBikeRepository
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaBikesUseCase
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
    fun `onAppResumedAfterAuth loads strava bikes`() = runTest {
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
                getStravaBikesUseCase = GetStravaBikesUseCase(repo)
            )

            viewModel.connectToStrava()
            advanceUntilIdle()

            viewModel.onAppResumedAfterAuth()
            advanceUntilIdle()

            assertEquals(1, viewModel.uiState.value.bikes.size)
            assertEquals("Strava Bike", viewModel.uiState.value.bikes.first().name)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

