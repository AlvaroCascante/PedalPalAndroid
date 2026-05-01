package com.quetoquenana.and.features.authentication.data.remote.dataSource

import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel

/**
 * Abstracts Firebase authentication operations into suspendable functions.
 */
interface FirebaseAuthDataSource {
    suspend fun getCurrentUserInfo(): FirebaseUserModel?
    suspend fun getIdToken(forceRefresh: Boolean = false): String
    suspend fun isEmailVerified(): Boolean
    suspend fun reloadUser()
    suspend fun sendEmailVerification()
    fun signOut()
    suspend fun signInWithEmail(email: String, password: String): FirebaseUserModel
    suspend fun signInWithGoogle(googleIdToken: String): FirebaseUserModel
    suspend fun signUpWithEmail(email: String, password: String): FirebaseUserModel
}
