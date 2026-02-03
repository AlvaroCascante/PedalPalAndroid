package com.quetoquenana.and.pedalpal.features.login.domain.useCase

import com.quetoquenana.and.pedalpal.features.login.domain.repository.FakeAuthRepository
import com.quetoquenana.and.pedalpal.util.firebaseUserInfoUnverified
import com.quetoquenana.and.pedalpal.util.firebaseUserInfoVerified
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckEmailVerifiedUseCaseTest {

    @Test
    fun `invoke returns true when repo reports verified`() = runBlocking {
        val fakeRepo = FakeAuthRepository(signInResult = firebaseUserInfoVerified)
        val useCase = CheckEmailVerifiedUseCase(authRepository = fakeRepo)

        val result = useCase()
        assertTrue(result)
    }

    @Test
    fun `invoke returns false when repo reports not verified`() = runBlocking {
        val fakeRepo = FakeAuthRepository(signInResult = firebaseUserInfoUnverified)
        val useCase = CheckEmailVerifiedUseCase(authRepository = fakeRepo)

        val result = useCase()
        assertFalse(result)
    }
}
