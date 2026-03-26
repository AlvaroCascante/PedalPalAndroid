package com.quetoquenana.and.features.auth.domain.usecase

import com.quetoquenana.and.features.auth.domain.model.CreateUserRequest
import com.quetoquenana.and.features.auth.domain.model.CreateUserUseCaseResult
import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: CreateUserRequest): CreateUserUseCaseResult {
        return authRepository.completeRegistration(request)
    }
}
