package com.quetoquenana.and.features.authentication.data.remote.dataSource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firebase-backed implementation of FirebaseAuthDataSource.
 * Uses FirebaseAuth (KTX) and Task.await() to provide suspend-friendly functions.
 */
class FirebaseAuthDataSourceImpl @Inject constructor() : FirebaseAuthDataSource {

    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    override suspend fun getCurrentUserInfo(): FirebaseUserModel? {
        val user = auth.currentUser ?: return null
        return withSessionRecovery {
            user.reload().await()
            toDomain(user)
        }
    }

    override suspend fun getIdToken(forceRefresh: Boolean): String {
        val user = auth.currentUser ?: throw IllegalStateException("No current Firebase user to get id token")
        return withSessionRecovery {
            val tokenResult = user.getIdToken(forceRefresh).await()
            tokenResult.token ?: throw IllegalStateException("Firebase ID token was null")
        }
    }

    override suspend fun isEmailVerified(): Boolean {
        val user = auth.currentUser ?: return false
        return withSessionRecovery {
            user.reload().await()
            user.isEmailVerified
        }
    }

    override suspend fun reloadUser() {
        val user = auth.currentUser ?: return
        withSessionRecovery {
            user.reload().await()
        }
    }

    override suspend fun sendEmailVerification() {
        val user = auth.currentUser ?: throw IllegalStateException("No current Firebase user to send verification")
        withSessionRecovery {
            user.sendEmailVerification().await()
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override suspend fun signInWithEmail(email: String, password: String): FirebaseUserModel {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Firebase user is null after email sign-in")
        return toDomain(user)
    }

    override suspend fun signInWithGoogle(googleIdToken: String): FirebaseUserModel {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: throw IllegalStateException("Firebase user is null after Google sign-in")
        return toDomain(user)
    }

    override suspend fun signUpWithEmail(email: String, password: String): FirebaseUserModel {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Firebase user is null after sign-up")
        return toDomain(user)
    }

    private fun toDomain(user: com.google.firebase.auth.FirebaseUser): FirebaseUserModel {
        val email = user.email ?: throw IllegalStateException("Firebase user email is null")
        return FirebaseUserModel(
            uid = user.uid,
            email = email,
            displayName = user.displayName,
            isEmailVerified = user.isEmailVerified,
        )
    }

    private suspend fun <T> withSessionRecovery(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            if (e.hasCauseMessage("INVALID_REFRESH_TOKEN")) {
                auth.signOut()
            }
            throw e
        }
    }

    private fun Throwable.hasCauseMessage(value: String): Boolean {
        return generateSequence(this as Throwable?) { it.cause }
            .any { throwable ->
                throwable.message?.contains(value, ignoreCase = true) == true
            }
    }
}
