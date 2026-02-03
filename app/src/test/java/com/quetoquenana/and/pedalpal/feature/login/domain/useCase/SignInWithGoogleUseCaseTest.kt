package com.quetoquenana.and.pedalpal.feature.login.domain.useCase

import com.quetoquenana.and.pedalpal.feature.login.domain.repository.FakeAuthRepository
import com.quetoquenana.and.pedalpal.util.firebaseUserInfoVerified
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignInWithGoogleUseCaseTest {

    @Test
    fun `invoke returns user on success`() = runBlocking {
        val user = firebaseUserInfoVerified
        val fakeRepo = FakeAuthRepository(signInResult = user)
        val useCase = SignInWithGoogleUseCase(authRepository = fakeRepo)

        val result = useCase(idToken = "id-token")

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when repo throws`() = runBlocking {
        val fakeRepo = FakeAuthRepository()
        val useCase = SignInWithGoogleUseCase(authRepository = fakeRepo)

        val result = useCase(idToken = "id-token")

        assertTrue(result.isFailure)
    }
}
