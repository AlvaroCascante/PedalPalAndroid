package com.quetoquenana.and.pedalpal.feature.login.domain.useCase

import com.quetoquenana.and.pedalpal.feature.login.domain.repository.AuthRepository
import javax.inject.Inject

class CheckEmailVerifiedUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Boolean {
        return authRepository.isEmailVerified()
    }
}
