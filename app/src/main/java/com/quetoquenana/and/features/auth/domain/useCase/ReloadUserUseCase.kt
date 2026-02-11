package com.quetoquenana.and.features.auth.domain.useCase

import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ReloadUserUseCase @Inject constructor(
    private val authRepository: com.quetoquenana.and.features.auth.domain.repository.AuthRepository,
) {
    suspend operator fun invoke() {
        return authRepository.reloadUser()
    }
}
