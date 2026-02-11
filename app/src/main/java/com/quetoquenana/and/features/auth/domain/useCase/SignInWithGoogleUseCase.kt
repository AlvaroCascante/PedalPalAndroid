package com.quetoquenana.and.features.auth.domain.useCase

import com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo
import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: com.quetoquenana.and.features.auth.domain.repository.AuthRepository,
) {
    suspend operator fun invoke(idToken: String): Result<com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo> =
        runCatching {
            authRepository.signInWithGoogle(googleIdToken = idToken)
        }
}
