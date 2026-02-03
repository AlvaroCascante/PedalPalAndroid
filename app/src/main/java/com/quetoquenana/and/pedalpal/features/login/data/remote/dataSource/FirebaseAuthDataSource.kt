package com.quetoquenana.and.pedalpal.features.login.data.remote.dataSource

import com.quetoquenana.and.pedalpal.features.login.domain.model.FirebaseUserInfo

/**
 * Abstracts Firebase authentication operations into suspendable functions.
 */
interface FirebaseAuthDataSource {
    suspend fun getCurrentUserInfo(): FirebaseUserInfo?
    suspend fun getIdToken(forceRefresh: Boolean = false): String
    suspend fun isEmailVerified(): Boolean
    suspend fun reloadUser()
    suspend fun sendEmailVerification()
    suspend fun signInWithEmail(email: String, password: String): FirebaseUserInfo
    suspend fun signInWithGoogle(googleIdToken: String): FirebaseUserInfo
    suspend fun signUpWithEmail(email: String, password: String): FirebaseUserInfo
}
