package com.quetoquenana.and.auth.domain.usecase

import com.quetoquenana.and.auth.domain.repository.FakeAuthRepository
import com.quetoquenana.and.features.auth.domain.usecase.SignInWithGoogleUseCase
import com.quetoquenana.and.util.firebaseUserInfoVerified
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
