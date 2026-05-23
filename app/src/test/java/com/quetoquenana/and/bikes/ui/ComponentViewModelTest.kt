package com.quetoquenana.and.bikes.ui

import androidx.lifecycle.SavedStateHandle
import com.quetoquenana.and.bikes.domain.repository.FakeBikeRepository
import com.quetoquenana.and.features.bikes.domain.model.ComponentType
import com.quetoquenana.and.features.bikes.domain.usecase.AddBikeComponentUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikeComponentTypesUseCase
import com.quetoquenana.and.features.bikes.ui.BikeComponentViewModel
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
class ComponentViewModelTest {

    @Test
    fun `init loads component types into state`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val viewModel = viewModel(
                repository = FakeBikeRepository(
                    componentTypes = listOf(
                        componentType(code = "CHAIN", description = "Cadena"),
                        componentType(code = "CASSETTE", description = "Cassette")
                    )
                )
            )

            advanceUntilIdle()

            assertEquals(false, viewModel.uiState.value.isLoadingComponentTypes)
            assertEquals(listOf("CHAIN", "CASSETTE"), viewModel.uiState.value.componentTypes.map { it.code })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `saveComponent uses selected type code and emits NavigateBikeDetail`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeBikeRepository(
                componentTypes = listOf(componentType(code = "CASSETTE", description = "Cassette"))
            )
            val viewModel = viewModel(repository = repository)
            advanceUntilIdle()

            val deferred = CompletableDeferred<BikeComponentViewModel.BikeComponentEvent>()
            val job = launch {
                viewModel.events.collect {
                    if (!deferred.isCompleted) deferred.complete(it)
                }
            }

            viewModel.onNameChanged("  Shimano Center3  ")
            viewModel.onTypeChanged("CASSETTE")
            viewModel.onBrandChanged(" Shimano ")
            viewModel.onModelChanged(" B07X-CER ")
            viewModel.onNotesChanged(" Chain center cassette ")
            viewModel.onOdometerChanged("123")
            viewModel.onUsageTimeChanged("456")
            viewModel.saveComponent()
            advanceUntilIdle()

            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertNotNull(event)
            assertTrue(event is BikeComponentViewModel.BikeComponentEvent.NavigateBikeDetail)
            assertEquals("Shimano Center3", repository.lastAddComponentRequest?.name)
            assertEquals("CASSETTE", repository.lastAddComponentRequest?.type)
            assertEquals("Shimano", repository.lastAddComponentRequest?.brand)
            assertEquals("B07X-CER", repository.lastAddComponentRequest?.model)
            assertEquals("Chain center cassette", repository.lastAddComponentRequest?.notes)
            assertEquals(123, repository.lastAddComponentRequest?.odometerKm)
            assertEquals(456, repository.lastAddComponentRequest?.usageTimeMinutes)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `loadComponentTypes failure emits error and stops loading`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val deferred = CompletableDeferred<BikeComponentViewModel.BikeComponentEvent>()
            val viewModel = viewModel(
                repository = FakeBikeRepository(
                    componentTypesFailure = IllegalStateException("Types unavailable")
                )
            )
            val job = launch {
                viewModel.events.collect {
                    if (!deferred.isCompleted) deferred.complete(it)
                }
            }

            viewModel.loadComponentTypes()
            advanceUntilIdle()

            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertEquals(false, viewModel.uiState.value.isLoadingComponentTypes)
            assertTrue(event is BikeComponentViewModel.BikeComponentEvent.ShowError)
            assertEquals(
                "Types unavailable",
                (event as BikeComponentViewModel.BikeComponentEvent.ShowError).message
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun viewModel(repository: FakeBikeRepository): BikeComponentViewModel {
        return BikeComponentViewModel(
            savedStateHandle = SavedStateHandle(mapOf("bikeId" to "bike-1")),
            addBikeComponentUseCase = AddBikeComponentUseCase(repository),
            getBikeComponentTypesUseCase = GetBikeComponentTypesUseCase(repository)
        )
    }

    private fun componentType(code: String, description: String): ComponentType {
        return ComponentType(
            id = "id-$code",
            category = "BIKE_COMPONENT_TYPE",
            code = code,
            codeDescription = description,
            status = "ACTIVE",
            position = 1
        )
    }
}
