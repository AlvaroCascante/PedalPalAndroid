package com.quetoquenana.and.auth.domain.usecase

import com.quetoquenana.and.auth.domain.repository.FakeAuthRepository
import com.quetoquenana.and.features.auth.domain.usecase.ReloadUserUseCase
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
