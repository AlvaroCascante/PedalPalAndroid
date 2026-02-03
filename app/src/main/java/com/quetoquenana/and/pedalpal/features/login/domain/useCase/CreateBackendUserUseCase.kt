package com.quetoquenana.and.pedalpal.features.login.domain.useCase

import com.quetoquenana.and.pedalpal.features.login.domain.model.AuthToken
import com.quetoquenana.and.pedalpal.features.login.domain.model.BackendCreateUserRequest
import com.quetoquenana.and.pedalpal.features.login.domain.repository.AuthRepository
import javax.inject.Inject

class CreateBackendUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(request: BackendCreateUserRequest): AuthToken {
        // obtain a fresh firebase id token and call backend create
        val idToken = authRepository.getFirebaseIdToken(forceRefresh = true)
        return authRepository.createBackendUser(request, idToken)
    }
}
