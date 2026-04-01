package com.quetoquenana.and.features.authentication.domain.repository

import com.quetoquenana.and.features.authentication.domain.model.CreateUserRequest
import com.quetoquenana.and.features.authentication.domain.model.CreateUserUseCaseResult
import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel
import com.quetoquenana.and.features.authentication.domain.model.SessionStatus

interface AuthRepository {
    suspend fun completeRegistration(request: CreateUserRequest): CreateUserUseCaseResult


    suspend fun hasActiveSession(): Boolean
    suspend fun restoreSession(): SessionStatus
    suspend fun logout()


    suspend fun getFirebaseIdToken(forceRefresh: Boolean = false): String
    suspend fun isEmailVerified(): Boolean
    suspend fun reloadUser()
    suspend fun sendEmailVerification()
    suspend fun signInWithEmail(email: String, password: String): FirebaseUserModel
    suspend fun signInWithGoogle(googleIdToken: String): FirebaseUserModel
    suspend fun signUpWithEmail(email: String, password: String): FirebaseUserModel
}