package com.quetoquenana.and.auth.domain.usecase

import com.quetoquenana.and.auth.domain.repository.FakeAuthRepository
import com.quetoquenana.and.features.authentication.domain.usecase.LogoutUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class LogoutUseCaseTest {

    @Test
    fun `invoke calls repository logout`() = runBlocking {
        val fakeRepo = FakeAuthRepository()
        val useCase = LogoutUseCase(authRepository = fakeRepo)

        useCase()

        assertTrue(fakeRepo.logoutCalled)
    }
}
