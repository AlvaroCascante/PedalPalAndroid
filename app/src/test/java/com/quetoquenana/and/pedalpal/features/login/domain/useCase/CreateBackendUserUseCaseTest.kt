package com.quetoquenana.and.pedalpal.features.login.domain.useCase

import com.quetoquenana.and.pedalpal.features.login.domain.model.BackendCreateUserRequest
import com.quetoquenana.and.pedalpal.features.login.domain.repository.FakeAuthRepository
import com.quetoquenana.and.pedalpal.util.backendUser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateBackendUserUseCaseTest {

    @Test
    fun `invoke obtains firebase token and calls backend create returning AuthToken`() = runBlocking {
        val fakeRepo = FakeAuthRepository()

        val useCase = CreateBackendUserUseCase(authRepository = fakeRepo)
        val request = BackendCreateUserRequest(user = backendUser, roleName = "USER")
        val result = useCase(request)

        // FakeAuthRepository returns a token-based AuthToken with accessToken == fake token
        assertEquals(fakeRepo.getFirebaseIdToken(forceRefresh = true), result.accessToken)
    }
}
