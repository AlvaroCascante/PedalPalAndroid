package com.quetoquenana.and.auth.domain.usecase

import com.quetoquenana.and.auth.domain.repository.FakeAuthRepository
import com.quetoquenana.and.features.auth.domain.model.CreateUserRequest
import com.quetoquenana.and.features.auth.domain.model.CreateUserUseCaseResult
import com.quetoquenana.and.features.auth.domain.usecase.CreateUserUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateUserUseCaseTest {

    @Test
    fun `invoke calls backend registration returning success`() {
        runBlocking {
            val fakeRepo = FakeAuthRepository()

            val useCase = CreateUserUseCase(authRepository = fakeRepo)
            val request = CreateUserRequest(
                nickname = "joe_nick",
                idNumber = "id-1",
                name = "John",
                lastname = "Doe"
            )
            val result = useCase(request)

            assertEquals(CreateUserUseCaseResult.Success(userId = "fake-user-id"), result)
            assertEquals(request, fakeRepo.completeRegistrationCalledWith)
        }
    }
}
