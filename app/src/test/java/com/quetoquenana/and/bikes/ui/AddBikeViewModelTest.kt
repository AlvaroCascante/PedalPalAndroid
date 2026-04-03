package com.quetoquenana.and.bikes.ui

import com.quetoquenana.and.bikes.domain.repository.FakeBikeRepository
import com.quetoquenana.and.features.bikes.domain.usecase.CreateBikeUseCase
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
            val viewModel = AddBikeViewModel(
                createBikeUseCase = CreateBikeUseCase(fakeRepository)
            )

            viewModel.onNameChanged("  Trek Domane  ")
            viewModel.onTypeChanged(" Road ")
            viewModel.onBrandChanged(" Trek ")
            viewModel.onModelChanged(" AL 2 ")
            viewModel.onYearChanged("2024")
            viewModel.onSerialNumberChanged(" SN-1 ")
            viewModel.onNotesChanged(" Weekend bike ")
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
            assertEquals("Road", fakeRepository.lastCreateRequest?.type)
            assertEquals("Trek", fakeRepository.lastCreateRequest?.brand)
            assertEquals("AL 2", fakeRepository.lastCreateRequest?.model)
            assertEquals(2024, fakeRepository.lastCreateRequest?.year)
            assertEquals("SN-1", fakeRepository.lastCreateRequest?.serialNumber)
            assertEquals("Weekend bike", fakeRepository.lastCreateRequest?.notes)
            assertEquals(true, fakeRepository.lastCreateRequest?.isPublic)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `saveBike without name emits validation error`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val viewModel = AddBikeViewModel(
                createBikeUseCase = CreateBikeUseCase(FakeBikeRepository())
            )
            viewModel.onTypeChanged("Road")

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
}

