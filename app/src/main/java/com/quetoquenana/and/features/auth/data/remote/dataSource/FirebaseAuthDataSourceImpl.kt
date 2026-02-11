package com.quetoquenana.and.features.auth.data.remote.dataSource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firebase-backed implementation of FirebaseAuthDataSource.
 * Uses FirebaseAuth (KTX) and Task.await() to provide suspend-friendly functions.
 */
class FirebaseAuthDataSourceImpl @Inject constructor() : FirebaseAuthDataSource {

    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    override suspend fun getCurrentUserInfo(): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo? {
        val user = auth.currentUser ?: return null
        user.reload().await()
        return toDomain(user)
    }

    override suspend fun getIdToken(forceRefresh: Boolean): String {
        val user = auth.currentUser ?: throw IllegalStateException("No current Firebase user to get id token")
        val tokenResult = user.getIdToken(forceRefresh).await()
        return tokenResult.token ?: throw IllegalStateException("Firebase ID token was null")
    }

    override suspend fun isEmailVerified(): Boolean {
        val user = auth.currentUser ?: return false
        user.reload().await()
        return user.isEmailVerified
    }

    override suspend fun reloadUser() {
        val user = auth.currentUser ?: return
        user.reload().await()
    }

    override suspend fun sendEmailVerification() {
        val user = auth.currentUser ?: throw IllegalStateException("No current Firebase user to send verification")
        user.sendEmailVerification().await()
    }

    override suspend fun signInWithEmail(email: String, password: String): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Firebase user is null after email sign-in")
        return toDomain(user)
    }

    override suspend fun signInWithGoogle(googleIdToken: String): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: throw IllegalStateException("Firebase user is null after Google sign-in")
        return toDomain(user)
    }

    override suspend fun signUpWithEmail(email: String, password: String): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Firebase user is null after sign-up")
        return toDomain(user)
    }

    private fun toDomain(user: com.google.firebase.auth.FirebaseUser): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo {
        return _root_ide_package_.com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo(
            uid = user.uid,
            email = user.email,
            displayName = user.displayName,
            isEmailVerified = user.isEmailVerified,
        )
    }
}
