package com.quetoquenana.and.features.auth.domain.usecase

import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ReloadUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() {
        return authRepository.reloadUser()
    }
}
