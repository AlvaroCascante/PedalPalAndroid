package com.quetoquenana.and.bikes.ui

import androidx.lifecycle.SavedStateHandle
import com.quetoquenana.and.bikes.domain.repository.FakeBikeRepository
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikeUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.UploadBikeProfileImageUseCase
import com.quetoquenana.and.features.bikes.ui.BikeDetailViewModel
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BikeDetailViewModelTest {

    @Test
    fun `uploadProfileImage emits success message and resets uploading state`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeBikeRepository(initialBikes = listOf(sampleBike()))
            val viewModel = viewModel(repository)
            advanceUntilIdle()
            val deferred = CompletableDeferred<BikeDetailViewModel.BikeDetailEvent>()
            val job = launch {
                viewModel.events.collect {
                    if (!deferred.isCompleted) deferred.complete(it)
                }
            }

            val request = MediaUploadRequest(
                name = "BikeProfile.png",
                altText = "Bike profile",
                contentType = "image/png",
                bytes = byteArrayOf(1, 2, 3),
                isPublic = false,
            )

            viewModel.uploadProfileImage(request)
            advanceUntilIdle()

            assertEquals(request, repository.lastUploadBikeProfileImageRequest)
            assertEquals(1, repository.uploadBikeProfileImageCallCount)
            assertFalse(viewModel.uiState.value.isUploadingProfileImage)
            assertEquals(
                BikeDetailViewModel.BikeDetailEvent.ShowMessage("Bike profile image updated"),
                withTimeoutOrNull(1_000) { deferred.await() },
            )
            job.cancel()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `uploadProfileImage emits error when repository upload fails`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeBikeRepository(
                initialBikes = listOf(sampleBike()),
                uploadBikeMediaFailure = IllegalStateException("upload failed"),
            )
            val viewModel = viewModel(repository)
            advanceUntilIdle()
            val deferred = CompletableDeferred<BikeDetailViewModel.BikeDetailEvent>()
            val job = launch {
                viewModel.events.collect {
                    if (!deferred.isCompleted) deferred.complete(it)
                }
            }

            viewModel.uploadProfileImage(
                MediaUploadRequest(
                    name = "BikeProfile.png",
                    altText = "Bike profile",
                    contentType = "image/png",
                    bytes = byteArrayOf(9),
                    isPublic = false,
                )
            )
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isUploadingProfileImage)
            val event = withTimeoutOrNull(1_000) { deferred.await() }
            assertTrue(event is BikeDetailViewModel.BikeDetailEvent.ShowError)
            assertEquals(
                "upload failed",
                (event as BikeDetailViewModel.BikeDetailEvent.ShowError).message,
            )
            job.cancel()
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun viewModel(repository: FakeBikeRepository): BikeDetailViewModel {
        return BikeDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("id" to "bike-1")),
            getBikeUseCase = GetBikeUseCase(repository),
            uploadBikeProfileImageUseCase = UploadBikeProfileImageUseCase(repository),
        )
    }

    private fun sampleBike(): Bike {
        return Bike(
            id = "bike-1",
            name = "Trek Domane",
            type = "ROAD",
            status = "ACTIVE",
            isPublic = false,
            isExternalSync = false,
            brand = "Trek",
            model = "Domane AL 2",
            year = 2024,
            serialNumber = "SN-1",
            notes = null,
            odometerKm = 1000.0,
            usageTimeMinutes = 3600,
            externalGearId = null,
            externalSyncProvider = "MANUAL",
        )
    }
}

