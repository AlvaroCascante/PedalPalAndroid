package com.quetoquenana.and.features.auth.domain.useCase

import com.quetoquenana.and.features.auth.domain.model.AuthToken
import com.quetoquenana.and.features.auth.domain.model.BackendCreateUserRequest
import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class CreateBackendUserUseCase @Inject constructor(
    private val authRepository: com.quetoquenana.and.features.auth.domain.repository.AuthRepository,
) {
    suspend operator fun invoke(request: com.quetoquenana.and.features.auth.domain.model.BackendCreateUserRequest): com.quetoquenana.and.features.auth.domain.model.AuthToken {
        // obtain a fresh firebase id token and call backend create
        val idToken = authRepository.getFirebaseIdToken(forceRefresh = true)
        return authRepository.createBackendUser(request, idToken)
    }
}
