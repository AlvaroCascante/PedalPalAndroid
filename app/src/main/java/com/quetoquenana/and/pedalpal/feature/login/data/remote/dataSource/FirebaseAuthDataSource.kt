package com.quetoquenana.and.pedalpal.feature.login.data.remote.dataSource

import com.quetoquenana.and.pedalpal.feature.login.domain.model.FirebaseUserInfo

/**
 * Abstracts Firebase authentication operations into suspendable functions.
 */
interface FirebaseAuthDataSource {
    suspend fun signInWithGoogle(googleIdToken: String): FirebaseUserInfo
    suspend fun signUpWithEmail(email: String, password: String): FirebaseUserInfo
    suspend fun signInWithEmail(email: String, password: String): FirebaseUserInfo
    suspend fun sendEmailVerification()
    suspend fun isEmailVerified(): Boolean
    suspend fun reloadUser()
    suspend fun getIdToken(forceRefresh: Boolean = false): String
}
