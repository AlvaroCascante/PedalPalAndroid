package com.quetoquenana.and.bikes.ui

import androidx.lifecycle.SavedStateHandle
import com.quetoquenana.and.bikes.domain.repository.FakeBikeRepository
import com.quetoquenana.and.features.bikes.domain.model.BikeMedia
import com.quetoquenana.and.features.bikes.domain.model.BikeMediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikeMediaUseCase
import com.quetoquenana.and.features.bikes.ui.BikeMediaViewModel
import com.quetoquenana.and.features.bikes.domain.usecase.UploadBikeMediaUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BikeMediaViewModelTest {

    @Test
    fun `loadMedia fetches fresh urls each time it is called`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeBikeRepository(
                bikeMedia = listOf(
                    BikeMedia(
                        id = "media-1",
                        contentType = "IMAGE_PNG",
                        provider = "Cloudflare",
                        isPrimary = false,
                        status = "Active",
                        name = "SecondBikeImage",
                        altText = "Alt text",
                        url = "https://example.com/image.png",
                        expiresAt = "2026-05-15T03:28:49Z"
                    )
                )
            )
            val viewModel = viewModel(repository)

            viewModel.loadMedia()
            advanceUntilIdle()
            viewModel.loadMedia()
            advanceUntilIdle()

            assertEquals(2, repository.getBikeMediaCallCount)
            assertEquals(listOf("media-1"), viewModel.uiState.value.media.map { it.id })
            assertNull(viewModel.uiState.value.errorMessage)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `loadMedia updates error when repository fails`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val viewModel = viewModel(
                FakeBikeRepository(bikeMediaFailure = IllegalStateException("Media failed"))
            )

            viewModel.loadMedia()
            advanceUntilIdle()

            assertEquals("Media failed", viewModel.uiState.value.errorMessage)
            assertEquals(false, viewModel.uiState.value.isLoading)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `uploadMedia uploads selected images then refreshes list`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeBikeRepository()
            val viewModel = viewModel(repository)
            val deferred = CompletableDeferred<BikeMediaViewModel.BikeMediaEvent>()
            val job = launch {
                viewModel.events.collect {
                    if (!deferred.isCompleted) deferred.complete(it)
                }
            }

            viewModel.uploadMedia(
                listOf(
                    BikeMediaUploadRequest(
                        name = "FirstBikeImage.png",
                        altText = "FirstBikeImage.png",
                        contentType = "image/png",
                        isPrimary = true,
                        bytes = byteArrayOf(1, 2, 3)
                    )
                )
            )
            advanceUntilIdle()

            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertEquals(1, repository.uploadBikeMediaCallCount)
            assertEquals(1, repository.getBikeMediaCallCount)
            assertEquals(listOf("FirstBikeImage.png"), viewModel.uiState.value.media.map { it.name })
            assertTrue(event is BikeMediaViewModel.BikeMediaEvent.ShowMessage)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun viewModel(repository: FakeBikeRepository): BikeMediaViewModel {
        return BikeMediaViewModel(
            savedStateHandle = SavedStateHandle(mapOf("id" to "bike-1")),
            getBikeMediaUseCase = GetBikeMediaUseCase(repository),
            uploadBikeMediaUseCase = UploadBikeMediaUseCase(repository)
        )
    }
}
