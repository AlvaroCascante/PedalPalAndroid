package com.quetoquenana.and.auth.domain.usecase

import com.quetoquenana.and.features.authentication.domain.model.SessionStatus
import com.quetoquenana.and.auth.domain.repository.FakeAuthRepository
import com.quetoquenana.and.features.authentication.domain.usecase.RestoreSessionUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RestoreSessionUseCaseTest {

    @Test
    fun `invoke returns session status from repository`() = runBlocking {
        val expected = SessionStatus.ProfileCompletionRequired
        val fakeRepo = FakeAuthRepository(sessionStatus = expected)
        val useCase = RestoreSessionUseCase(authRepository = fakeRepo)

        val result = useCase()

        assertTrue(fakeRepo.restoreSessionCalled)
        assertEquals(expected, result)
    }
}
