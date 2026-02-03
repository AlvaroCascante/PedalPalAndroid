package com.quetoquenana.and.pedalpal.feature.login.domain.useCase

import com.quetoquenana.and.pedalpal.feature.login.domain.repository.FakeAuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ReloadUserUseCaseTest {

    @Test
    fun `invoke calls repository reloadUser`() = runBlocking {
        val fakeRepo = FakeAuthRepository()
        val useCase = ReloadUserUseCase(authRepository = fakeRepo)

        useCase()

        assertTrue(fakeRepo.reloadUserCalled)
    }
}
