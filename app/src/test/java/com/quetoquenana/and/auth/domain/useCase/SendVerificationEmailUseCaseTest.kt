package com.quetoquenana.and.auth.domain.useCase

import com.quetoquenana.and.auth.domain.repository.FakeAuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class SendVerificationEmailUseCaseTest {

    @Test
    fun `invoke calls repository to send verification`() = runBlocking {
        val fakeRepo = FakeAuthRepository()
        val useCase = SendVerificationEmailUseCase(authRepository = fakeRepo)

        useCase()

        // FakeAuthRepository toggles a flag when sendEmailVerification is called
        assertTrue(fakeRepo.sendEmailVerificationCalled)
    }
}
