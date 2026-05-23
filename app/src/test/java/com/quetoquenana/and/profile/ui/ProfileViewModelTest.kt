package com.quetoquenana.and.profile.ui

import com.quetoquenana.and.auth.domain.repository.FakeAuthRepository
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.authentication.domain.usecase.LogoutUseCase
import com.quetoquenana.and.features.profile.domain.model.Profile
import com.quetoquenana.and.features.profile.domain.usecase.GetProfileUseCase
import com.quetoquenana.and.features.profile.domain.usecase.UploadProfilePhotoUseCase
import com.quetoquenana.and.profile.domain.repository.FakeProfileRepository
import com.quetoquenana.and.features.profile.ui.ProfileViewModel
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
class ProfileViewModelTest {

    @Test
    fun `init loads profile data into ui state`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val fakeProfileRepo = FakeProfileRepository(profile = profile())
            val fakeAuthRepo = FakeAuthRepository()
            val viewModel = ProfileViewModel(
                getProfileUseCase = GetProfileUseCase(profileRepository = fakeProfileRepo),
                logoutUseCase = LogoutUseCase(authRepository = fakeAuthRepo),
                uploadProfilePhotoUseCase = UploadProfilePhotoUseCase(profileRepository = fakeProfileRepo)
            )

            advanceUntilIdle()

            assertEquals("Test", viewModel.uiState.value.profile?.name)
            assertEquals("Rider", viewModel.uiState.value.profile?.lastname)
            assertFalse(viewModel.uiState.value.isLoading)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onLogoutClicked emits navigation event on success`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val fakeProfileRepo = FakeProfileRepository(profile = profile())
            val fakeAuthRepo = FakeAuthRepository()
            val viewModel = ProfileViewModel(
                getProfileUseCase = GetProfileUseCase(profileRepository = fakeProfileRepo),
                logoutUseCase = LogoutUseCase(authRepository = fakeAuthRepo),
                uploadProfilePhotoUseCase = UploadProfilePhotoUseCase(profileRepository = fakeProfileRepo)
            )
            advanceUntilIdle()
            val eventDeferred = CompletableDeferred<ProfileViewModel.ProfileEvent>()
            val job = launch {
                viewModel.events.collect { event ->
                    if (!eventDeferred.isCompleted) eventDeferred.complete(event)
                }
            }

            viewModel.onLogoutClicked()
            advanceUntilIdle()

            assertEquals(
                ProfileViewModel.ProfileEvent.NavigateStartup,
                withTimeoutOrNull(1_000) { eventDeferred.await() }
            )
            assertTrue(fakeAuthRepo.logoutCalled)
            assertFalse(viewModel.uiState.value.isLoggingOut)
            job.cancel()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onLogoutClicked emits error event on failure`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val fakeProfileRepo = FakeProfileRepository(profile = profile())
            val fakeAuthRepo = FakeAuthRepository(logoutException = IllegalStateException("boom"))
            val viewModel = ProfileViewModel(
                getProfileUseCase = GetProfileUseCase(profileRepository = fakeProfileRepo),
                logoutUseCase = LogoutUseCase(authRepository = fakeAuthRepo),
                uploadProfilePhotoUseCase = UploadProfilePhotoUseCase(profileRepository = fakeProfileRepo)
            )
            advanceUntilIdle()
            val eventDeferred = CompletableDeferred<ProfileViewModel.ProfileEvent>()
            val job = launch {
                viewModel.events.collect { event ->
                    if (!eventDeferred.isCompleted) eventDeferred.complete(event)
                }
            }

            viewModel.onLogoutClicked()
            advanceUntilIdle()

            assertEquals(
                ProfileViewModel.ProfileEvent.ShowError("boom"),
                withTimeoutOrNull(1_000) { eventDeferred.await() }
            )
            assertTrue(fakeAuthRepo.logoutCalled)
            assertFalse(viewModel.uiState.value.isLoggingOut)
            job.cancel()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onProfilePhotoSelected updates ui state and emits success message`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val updatedProfile = profile(
                photoUrl = "https://example.com/updated.png",
                profileMediaId = "media-1"
            )
            val fakeProfileRepo = FakeProfileRepository(
                profile = profile(),
                uploadProfilePhotoResult = updatedProfile
            )
            val fakeAuthRepo = FakeAuthRepository()
            val viewModel = ProfileViewModel(
                getProfileUseCase = GetProfileUseCase(profileRepository = fakeProfileRepo),
                logoutUseCase = LogoutUseCase(authRepository = fakeAuthRepo),
                uploadProfilePhotoUseCase = UploadProfilePhotoUseCase(profileRepository = fakeProfileRepo)
            )
            advanceUntilIdle()
            val eventDeferred = CompletableDeferred<ProfileViewModel.ProfileEvent>()
            val job = launch {
                viewModel.events.collect { event ->
                    if (!eventDeferred.isCompleted) eventDeferred.complete(event)
                }
            }

            val request = MediaUploadRequest(
                name = "Profile",
                altText = "Profile image",
                contentType = "image/png",
                bytes = byteArrayOf(1, 2, 3),
                isPublic = true,
            )

            viewModel.onProfilePhotoSelected(request)
            advanceUntilIdle()

            assertEquals(request, fakeProfileRepo.uploadProfilePhotoCalledWith)
            assertEquals("https://example.com/updated.png", viewModel.uiState.value.profile?.photoUrl)
            assertEquals("media-1", viewModel.uiState.value.profile?.profileMediaId)
            assertTrue(fakeProfileRepo.getProfileCallCount >= 2)
            assertFalse(viewModel.uiState.value.isUploadingPhoto)
            assertEquals(
                ProfileViewModel.ProfileEvent.ShowMessage("Profile picture updated"),
                withTimeoutOrNull(1_000) { eventDeferred.await() }
            )
            job.cancel()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onProfilePhotoSelected emits error on upload failure`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val fakeProfileRepo = FakeProfileRepository(
                profile = profile(),
                uploadProfilePhotoException = IllegalStateException("upload failed")
            )
            val fakeAuthRepo = FakeAuthRepository()
            val viewModel = ProfileViewModel(
                getProfileUseCase = GetProfileUseCase(profileRepository = fakeProfileRepo),
                logoutUseCase = LogoutUseCase(authRepository = fakeAuthRepo),
                uploadProfilePhotoUseCase = UploadProfilePhotoUseCase(profileRepository = fakeProfileRepo)
            )
            advanceUntilIdle()
            val eventDeferred = CompletableDeferred<ProfileViewModel.ProfileEvent>()
            val job = launch {
                viewModel.events.collect { event ->
                    if (!eventDeferred.isCompleted) eventDeferred.complete(event)
                }
            }

            viewModel.onProfilePhotoSelected(
                MediaUploadRequest(
                    name = "Profile",
                    altText = "Profile image",
                    contentType = "image/png",
                    bytes = byteArrayOf(4, 5, 6),
                    isPublic = true,
                )
            )
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isUploadingPhoto)
            assertEquals(
                ProfileViewModel.ProfileEvent.ShowError("upload failed"),
                withTimeoutOrNull(1_000) { eventDeferred.await() }
            )
            job.cancel()
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun profile(
        photoUrl: String? = null,
        profileMediaId: String? = null,
    ): Profile {
        return Profile(
            id = "backend-user-id",
            name = "Test",
            lastname = "Rider",
            idNumber = "123456789",
            username = "test@example.com",
            externalId = "firebase-user-id",
            provider = "PASSWORD",
            nickname = "testrider",
            userStatus = "ACTIVE",
            photoUrl = photoUrl,
            profileMediaId = profileMediaId,
        )
    }
}


