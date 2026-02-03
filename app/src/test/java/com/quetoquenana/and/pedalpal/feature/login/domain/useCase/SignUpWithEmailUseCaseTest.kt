package com.quetoquenana.and.pedalpal.feature.login.domain.useCase

import com.quetoquenana.and.pedalpal.feature.login.domain.repository.FakeAuthRepository
import com.quetoquenana.and.pedalpal.util.firebaseUserInfoUnverified
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignUpWithEmailUseCaseTest {

    @Test
    fun `invoke returns user on success`() = runBlocking {
        val user = firebaseUserInfoUnverified
        val fakeRepo = FakeAuthRepository(signUpResult = user)
        val useCase = SignUpWithEmailUseCase(authRepository = fakeRepo)

        val result = useCase("s@example.com", "pwd")

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when repo throws`() = runBlocking {
        val fakeRepo = FakeAuthRepository()
        val useCase = SignUpWithEmailUseCase(authRepository = fakeRepo)

        val result = useCase("s@example.com", "pwd")

        assertTrue(result.isFailure)
    }
}
