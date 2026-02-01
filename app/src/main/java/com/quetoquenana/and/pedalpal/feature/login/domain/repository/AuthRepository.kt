package com.quetoquenana.and.pedalpal.feature.login.domain.repository

import com.quetoquenana.and.pedalpal.feature.login.domain.model.AuthToken
import com.quetoquenana.and.pedalpal.feature.login.domain.model.BackendCreateUserRequest
import com.quetoquenana.and.pedalpal.feature.login.domain.model.FirebaseUserInfo

interface AuthRepository {
    suspend fun createBackendUser(request: BackendCreateUserRequest, firebaseIdToken: String): AuthToken
    suspend fun getCurrentUserInfo(): FirebaseUserInfo?
    suspend fun getFirebaseIdToken(forceRefresh: Boolean = false): String
    suspend fun isEmailVerified(): Boolean
    suspend fun reloadUser()
    suspend fun sendEmailVerification()
    suspend fun signInWithEmail(email: String, password: String): FirebaseUserInfo
    suspend fun signInWithGoogle(googleIdToken: String): FirebaseUserInfo
    suspend fun signUpWithEmail(email: String, password: String): FirebaseUserInfo
}