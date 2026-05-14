package com.quetoquenana.and.bikes.ui

import com.quetoquenana.and.bikes.domain.repository.FakeBikeRepository
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikesUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.ObserveBikesUseCase
import com.quetoquenana.and.features.bikes.ui.BikesViewModel
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
class BikesViewModelTest {

    @Test
    fun `bikes update automatically when repository flow emits`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeBikeRepository()
            val viewModel = viewModel(repository)
            advanceUntilIdle()

            repository.emitBikes(listOf(bike(id = "bike-1", name = "Trek Domane", type = BikeType.ROAD)))
            advanceUntilIdle()

            assertEquals(listOf("Trek Domane"), viewModel.uiState.value.bikes.map { it.name })
            assertEquals(false, viewModel.uiState.value.isLoading)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `selected type filters emitted bikes`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeBikeRepository(
                initialBikes = listOf(
                    bike(id = "bike-1", name = "Road bike", type = BikeType.ROAD),
                    bike(id = "bike-2", name = "Gravel bike", type = BikeType.GRAVEL)
                )
            )
            val viewModel = viewModel(repository)
            advanceUntilIdle()

            viewModel.onTypeSelected(BikeType.GRAVEL)

            assertEquals(listOf("Gravel bike"), viewModel.uiState.value.filteredBikes.map { it.name })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `flow emission updates list without another manual reload`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeBikeRepository()
            val viewModel = viewModel(repository)
            advanceUntilIdle()
            val callsAfterInit = repository.getBikesCallCount

            repository.emitBikes(listOf(bike(id = "bike-3", name = "City bike", type = BikeType.HYBRID)))
            advanceUntilIdle()

            assertEquals(callsAfterInit, repository.getBikesCallCount)
            assertEquals(listOf("City bike"), viewModel.uiState.value.bikes.map { it.name })
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun viewModel(repository: FakeBikeRepository): BikesViewModel {
        return BikesViewModel(
            observeBikesUseCase = ObserveBikesUseCase(repository),
            getBikesUseCase = GetBikesUseCase(repository)
        )
    }

    private fun bike(id: String, name: String, type: BikeType): Bike {
        return Bike(
            id = id,
            name = name,
            type = type.name,
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
