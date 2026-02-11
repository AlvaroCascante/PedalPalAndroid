package com.quetoquenana.and.features.auth.data.remote.dataSource

import com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo

/**
 * Abstracts Firebase authentication operations into suspendable functions.
 */
interface FirebaseAuthDataSource {
    suspend fun getCurrentUserInfo(): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo?
    suspend fun getIdToken(forceRefresh: Boolean = false): String
    suspend fun isEmailVerified(): Boolean
    suspend fun reloadUser()
    suspend fun sendEmailVerification()
    suspend fun signInWithEmail(email: String, password: String): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo
    suspend fun signInWithGoogle(googleIdToken: String): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo
    suspend fun signUpWithEmail(email: String, password: String): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo
}
