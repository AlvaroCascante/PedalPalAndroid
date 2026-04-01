package com.quetoquenana.and.features.authentication.domain.repository

import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel

interface FirebaseRepository {

    suspend fun getCurrentUserInfo(): FirebaseUserModel?

    suspend fun getFirebaseIdToken(forceRefresh: Boolean = false): String
}