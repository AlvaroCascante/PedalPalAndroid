package com.quetoquenana.and.features.auth.domain.usecase

import com.quetoquenana.and.features.auth.domain.model.SessionStatus
import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RestoreSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(): SessionStatus {
        return authRepository.restoreSession()
    }
}