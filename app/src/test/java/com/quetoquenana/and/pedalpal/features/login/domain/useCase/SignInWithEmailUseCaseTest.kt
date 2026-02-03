package com.quetoquenana.and.pedalpal.features.login.domain.useCase

import com.quetoquenana.and.pedalpal.features.login.domain.repository.FakeAuthRepository
import com.quetoquenana.and.pedalpal.util.firebaseUserInfoVerified
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SignInWithEmailUseCaseTest {

    @Test
    fun `sign in with email returns user on success`() = runBlocking {
        val expected = firebaseUserInfoVerified
        val fakeRepo = FakeAuthRepository(signInResult = expected)
        val useCase = SignInWithEmailUseCase(authRepository = fakeRepo)

        val result = useCase(email = "a@b.com", password = "pwd")
        val user = result.getOrNull()

        assertEquals(expected, user)
    }

    @Test
    fun `sign in with email returns failure when repo throws`() = runBlocking {
        val fakeRepo = FakeAuthRepository(signInResult = null)
        val useCase = SignInWithEmailUseCase(authRepository = fakeRepo)

        val result = useCase(email = "x@example.com", password = "pwd")
        assert(result.isFailure)
    }
}
