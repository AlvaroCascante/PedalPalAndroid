package com.quetoquenana.and.features.auth.domain.repository

import com.quetoquenana.and.features.auth.domain.model.FirebaseUserModel

interface FirebaseRepository {

    suspend fun getCurrentUserInfo(): FirebaseUserModel?

    suspend fun getFirebaseIdToken(forceRefresh: Boolean = false): String
}