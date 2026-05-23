package com.quetoquenana.and.home.ui

import com.quetoquenana.and.features.home.domain.usecase.GetHomeContentUseCase
import com.quetoquenana.and.features.home.ui.HeaderSection
import com.quetoquenana.and.features.home.ui.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @Test
    fun `loadData when use case throws exits loading and shows create bike header`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val getHomeContentUseCase = mockk<GetHomeContentUseCase>()
            coEvery { getHomeContentUseCase.invoke() } throws IllegalStateException("Unexpected failure")

            val viewModel = HomeViewModel(getHomeContentUseCase = getHomeContentUseCase)

            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(HeaderSection.NoBikes(), viewModel.uiState.value.headerSection)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

