package com.quetoquenana.and.features.authentication.domain.usecase

import com.quetoquenana.and.features.authentication.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserDisplayNameUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): String? = authRepository.getCurrentUserDisplayName()
}
