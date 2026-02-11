package com.quetoquenana.and.auth.ui

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.quetoquenana.and.auth.domain.repository.FakeAuthRepository
import com.quetoquenana.and.features.auth.domain.useCase.CheckEmailVerifiedUseCase
import com.quetoquenana.and.features.auth.domain.useCase.ReloadUserUseCase
import com.quetoquenana.and.features.auth.domain.useCase.SendVerificationEmailUseCase
import com.quetoquenana.and.features.auth.domain.useCase.SignInWithEmailUseCase
import com.quetoquenana.and.features.auth.domain.useCase.SignInWithGoogleUseCase
import com.quetoquenana.and.features.auth.domain.useCase.SignUpWithEmailUseCase
import com.quetoquenana.and.util.firebaseUserInfoUnverified
import com.quetoquenana.and.util.firebaseUserInfoVerified
import io.mockk.mockk
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
class LoginViewModelTest {

    @Test
    fun `onContinueWithEmailSubmit when sign in success verified emits NavigateCompleteProfile`() = runTest {
        // Use a test dispatcher for Dispatchers.Main so ViewModelScope coroutines run here
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val signedUser = firebaseUserInfoVerified

            // Fake repository configured to return the signed user for sign-in
            val fakeRepo = FakeAuthRepository(signInResult = signedUser)

            // Instantiate real use-cases backed by the fake repository
            val signInWithEmail = SignInWithEmailUseCase(authRepository = fakeRepo)
            val signUpWithEmail = SignUpWithEmailUseCase(authRepository = fakeRepo)
            val sendVerificationEmail = SendVerificationEmailUseCase(authRepository = fakeRepo)
            val checkEmailVerified = CheckEmailVerifiedUseCase(authRepository = fakeRepo)
            val reloadUser = ReloadUserUseCase(authRepository = fakeRepo)
            val signInWithGoogle = SignInWithGoogleUseCase(authRepository = fakeRepo)
            val googleClient = mockk<GoogleSignInClient>(relaxed = true)

            val viewModel = AuthViewModel(
                checkEmailVerified = checkEmailVerified,
                googleSignInClient = googleClient,
                sendVerificationEmail = sendVerificationEmail,
                signInWithGoogle = signInWithGoogle,
                signInWithEmail = signInWithEmail,
                signUpWithEmail = signUpWithEmail,
                reloadUser = reloadUser
            )

            // Set email/password
            viewModel.onEmailChanged("x@example.com")
            viewModel.onPasswordChanged("pwd")

            val deferred = CompletableDeferred<AuthViewModel.AuthUiEvent>()

            // collect one event
            val job = launch {
                viewModel.uiEvents.collect { ev ->
                    if (!deferred.isCompleted) deferred.complete(ev)
                }
            }

            viewModel.onContinueWithEmailSubmit()

            // Let scheduled coroutines run
            advanceUntilIdle()

            // Wait briefly for the event
            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertNotNull("Expected a LoginUiEvent but none was emitted", event)
            assertTrue(event is AuthViewModel.AuthUiEvent.NavigateCompleteProfile)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onContinueWithEmailSubmit when sign in fails then sign up sends verification and sets state`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val signUpUser = firebaseUserInfoUnverified
            // signIn will fail (no signInResult), signUp should succeed
            val fakeRepo = FakeAuthRepository(signInResult = null, signUpResult = signUpUser)

            val signInWithEmail = SignInWithEmailUseCase(authRepository = fakeRepo)
            val signUpWithEmail = SignUpWithEmailUseCase(authRepository = fakeRepo)
            val sendVerificationEmail = SendVerificationEmailUseCase(authRepository = fakeRepo)
            val checkEmailVerified = CheckEmailVerifiedUseCase(authRepository = fakeRepo)
            val reloadUser = ReloadUserUseCase(authRepository = fakeRepo)
            val signInWithGoogle = SignInWithGoogleUseCase(authRepository = fakeRepo)
            val googleClient = mockk<GoogleSignInClient>(relaxed = true)

            val viewModel = AuthViewModel(
                checkEmailVerified = checkEmailVerified,
                googleSignInClient = googleClient,
                sendVerificationEmail = sendVerificationEmail,
                signInWithGoogle = signInWithGoogle,
                signInWithEmail = signInWithEmail,
                signUpWithEmail = signUpWithEmail,
                reloadUser = reloadUser
            )

            viewModel.onEmailChanged("s@example.com")
            viewModel.onPasswordChanged("pwd")

            viewModel.onContinueWithEmailSubmit()
            advanceUntilIdle()

            // After sign up, the repository should have sendEmailVerification called and uiState updated
            assertTrue(fakeRepo.sendEmailVerificationCalled)
            assertTrue(viewModel.uiState.value.isEmailVerificationSent)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onResendVerificationEmail calls sendVerificationEmail`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val fakeRepo = FakeAuthRepository()

            val sendVerificationEmail = SendVerificationEmailUseCase(authRepository = fakeRepo)
            val viewModel = AuthViewModel(
                checkEmailVerified = CheckEmailVerifiedUseCase(authRepository = fakeRepo),
                googleSignInClient = mockk(relaxed = true),
                sendVerificationEmail = sendVerificationEmail,
                signInWithGoogle = SignInWithGoogleUseCase(authRepository = fakeRepo),
                signInWithEmail = SignInWithEmailUseCase(authRepository = fakeRepo),
                signUpWithEmail = SignUpWithEmailUseCase(authRepository = fakeRepo),
                reloadUser = ReloadUserUseCase(authRepository = fakeRepo)
            )

            viewModel.onResendVerificationEmail()
            advanceUntilIdle()

            assertTrue(fakeRepo.sendEmailVerificationCalled)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onGoogleIdTokenReceived success emits NavigateCompleteProfile`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val user = firebaseUserInfoVerified
            val fakeRepo = FakeAuthRepository(signInResult = user)

            val signInWithGoogle = SignInWithGoogleUseCase(authRepository = fakeRepo)
            val viewModel = AuthViewModel(
                checkEmailVerified = CheckEmailVerifiedUseCase(authRepository = fakeRepo),
                googleSignInClient = mockk(relaxed = true),
                sendVerificationEmail = SendVerificationEmailUseCase(authRepository = fakeRepo),
                signInWithGoogle = signInWithGoogle,
                signInWithEmail = SignInWithEmailUseCase(authRepository = fakeRepo),
                signUpWithEmail = SignUpWithEmailUseCase(authRepository = fakeRepo),
                reloadUser = ReloadUserUseCase(authRepository = fakeRepo)
            )

            val deferred = CompletableDeferred<AuthViewModel.AuthUiEvent>()
            val job = launch { viewModel.uiEvents.collect { if (!deferred.isCompleted) deferred.complete(it) } }

            viewModel.onGoogleIdTokenReceived("id-token")
            advanceUntilIdle()

            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertNotNull(event)
            assertTrue(event is AuthViewModel.AuthUiEvent.NavigateCompleteProfile)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onCheckEmailVerified emits ShowError when not verified and NavigateCompleteProfile when verified`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            // first case: not verified
            val userNotVerified = firebaseUserInfoUnverified
            val fakeRepo1 = FakeAuthRepository(signInResult = userNotVerified)
            val viewModel1 = AuthViewModel(
                checkEmailVerified = CheckEmailVerifiedUseCase(authRepository = fakeRepo1),
                googleSignInClient = mockk(relaxed = true),
                sendVerificationEmail = SendVerificationEmailUseCase(authRepository = fakeRepo1),
                signInWithGoogle = SignInWithGoogleUseCase(authRepository = fakeRepo1),
                signInWithEmail = SignInWithEmailUseCase(authRepository = fakeRepo1),
                signUpWithEmail = SignUpWithEmailUseCase(authRepository = fakeRepo1),
                reloadUser = ReloadUserUseCase(authRepository = fakeRepo1)
            )

            val deferred1 = CompletableDeferred<AuthViewModel.AuthUiEvent>()
            val job1 = launch { viewModel1.uiEvents.collect { if (!deferred1.isCompleted) deferred1.complete(it) } }

            viewModel1.onCheckEmailVerified()
            advanceUntilIdle()

            val event1 = withTimeoutOrNull(1_000) { deferred1.await() }
            job1.cancel()

            assertNotNull(event1)
            assertTrue(event1 is AuthViewModel.AuthUiEvent.ShowError)

            // second case: verified
            val userVerified = firebaseUserInfoVerified
            val fakeRepo2 = FakeAuthRepository(signInResult = userVerified)
            val viewModel2 = AuthViewModel(
                checkEmailVerified = CheckEmailVerifiedUseCase(authRepository = fakeRepo2),
                googleSignInClient = mockk(relaxed = true),
                sendVerificationEmail = SendVerificationEmailUseCase(authRepository = fakeRepo2),
                signInWithGoogle = SignInWithGoogleUseCase(authRepository = fakeRepo2),
                signInWithEmail = SignInWithEmailUseCase(authRepository = fakeRepo2),
                signUpWithEmail = SignUpWithEmailUseCase(authRepository = fakeRepo2),
                reloadUser = ReloadUserUseCase(authRepository = fakeRepo2)
            )

            val deferred2 = CompletableDeferred<AuthViewModel.AuthUiEvent>()
            val job2 = launch { viewModel2.uiEvents.collect { if (!deferred2.isCompleted) deferred2.complete(it) } }

            viewModel2.onCheckEmailVerified()
            advanceUntilIdle()

            val event2 = withTimeoutOrNull(1_000) { deferred2.await() }
            job2.cancel()

            assertNotNull(event2)
            assertTrue(event2 is AuthViewModel.AuthUiEvent.NavigateCompleteProfile)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onGoogleSignInFailed emits ShowError`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val fakeRepo = FakeAuthRepository()
            val viewModel = AuthViewModel(
                checkEmailVerified = CheckEmailVerifiedUseCase(authRepository = fakeRepo),
                googleSignInClient = mockk(relaxed = true),
                sendVerificationEmail = SendVerificationEmailUseCase(authRepository = fakeRepo),
                signInWithGoogle = SignInWithGoogleUseCase(authRepository = fakeRepo),
                signInWithEmail = SignInWithEmailUseCase(authRepository = fakeRepo),
                signUpWithEmail = SignUpWithEmailUseCase(authRepository = fakeRepo),
                reloadUser = ReloadUserUseCase(authRepository = fakeRepo)
            )

            val deferred = CompletableDeferred<AuthViewModel.AuthUiEvent>()
            val job = launch { viewModel.uiEvents.collect { if (!deferred.isCompleted) deferred.complete(it) } }

            viewModel.onGoogleSignInFailed("error msg")
            advanceUntilIdle()

            val event = withTimeoutOrNull(1_000) { deferred.await() }
            job.cancel()

            assertNotNull(event)
            assertTrue(event is AuthViewModel.AuthUiEvent.ShowError)
            assertEquals("error msg", (event as AuthViewModel.AuthUiEvent.ShowError).message)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
