package com.quetoquenana.and.features.auth.domain.usecase

import com.quetoquenana.and.features.auth.domain.model.FirebaseUserModel
import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<FirebaseUserModel> =
        runCatching {
            authRepository.signUpWithEmail(email, password)
        }
}
