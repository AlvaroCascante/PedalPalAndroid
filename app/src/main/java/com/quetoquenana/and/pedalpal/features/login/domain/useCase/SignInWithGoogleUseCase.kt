package com.quetoquenana.and.pedalpal.features.login.domain.useCase

import com.quetoquenana.and.pedalpal.features.login.domain.model.FirebaseUserInfo
import com.quetoquenana.and.pedalpal.features.login.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(idToken: String): Result<FirebaseUserInfo> =
        runCatching {
            authRepository.signInWithGoogle(googleIdToken = idToken)
        }
}
