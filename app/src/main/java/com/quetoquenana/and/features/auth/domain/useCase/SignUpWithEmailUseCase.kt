package com.quetoquenana.and.features.auth.domain.useCase

import com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo
import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpWithEmailUseCase @Inject constructor(
    private val authRepository: com.quetoquenana.and.features.auth.domain.repository.AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo> =
        runCatching {
            authRepository.signUpWithEmail(email, password)
        }
}
