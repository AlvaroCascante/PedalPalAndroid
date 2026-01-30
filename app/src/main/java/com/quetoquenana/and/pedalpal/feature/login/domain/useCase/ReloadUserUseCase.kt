package com.quetoquenana.and.pedalpal.feature.login.domain.useCase

import com.quetoquenana.and.pedalpal.feature.login.domain.model.FirebaseUserInfo
import com.quetoquenana.and.pedalpal.feature.login.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): FirebaseUserInfo {
        return authRepository.signInWithEmail(email, password)
    }
}
