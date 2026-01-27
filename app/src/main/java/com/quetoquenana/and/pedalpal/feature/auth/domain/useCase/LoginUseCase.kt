package com.quetoquenana.and.pedalpal.feature.auth.domain.useCase

import com.quetoquenana.and.pedalpal.feature.auth.domain.model.AuthToken
import com.quetoquenana.and.pedalpal.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(username: String, password: String): AuthToken {
        // TODO: add validation / trimming rules if needed
        return authRepository.login(username = username, password = password)
    }
}