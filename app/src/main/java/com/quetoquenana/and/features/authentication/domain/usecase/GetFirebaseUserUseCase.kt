package com.quetoquenana.and.features.authentication.domain.usecase

import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel
import com.quetoquenana.and.features.authentication.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetFirebaseUserUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(): FirebaseUserModel {
        val user = firebaseRepository.getCurrentUserInfo()

        return FirebaseUserModel(
            uid = user?.uid ?: "",
            email = user?.email ?: "",
            displayName = user?.displayName ?: "",
            isEmailVerified = user?.isEmailVerified ?: false
        )
    }
}
