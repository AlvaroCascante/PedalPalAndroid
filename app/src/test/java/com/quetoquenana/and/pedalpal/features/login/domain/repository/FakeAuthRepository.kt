package com.quetoquenana.and.pedalpal.features.login.domain.repository

import com.quetoquenana.and.pedalpal.features.login.domain.model.AuthToken
import com.quetoquenana.and.pedalpal.features.login.domain.model.BackendCreateUserRequest
import com.quetoquenana.and.pedalpal.features.login.domain.model.FirebaseUserInfo

/**
 * Reusable fake AuthRepository for unit tests. Configure returned values via constructor.
 */
class FakeAuthRepository(
    private val signInResult: FirebaseUserInfo? = null,
    private val signUpResult: FirebaseUserInfo? = null,
    private val token: String = "fake-token"
) : AuthRepository {
    var sendEmailVerificationCalled = false
    var createBackendUserCalledWith: BackendCreateUserRequest? = null
    var reloadUserCalled = false

    override suspend fun createBackendUser(request: BackendCreateUserRequest, firebaseIdToken: String): AuthToken {
        createBackendUserCalledWith = request
        return AuthToken(accessToken = token, refreshToken = "", expiresIn = 0L)
    }

    override suspend fun getCurrentUserInfo(): FirebaseUserInfo? = signInResult

    override suspend fun getFirebaseIdToken(forceRefresh: Boolean): String = token

    override suspend fun isEmailVerified(): Boolean = signInResult?.isEmailVerified ?: false

    override suspend fun reloadUser() {
        reloadUserCalled = true
    }

    override suspend fun sendEmailVerification() {
        sendEmailVerificationCalled = true
    }

    override suspend fun signInWithEmail(email: String, password: String): FirebaseUserInfo {
        return signInResult ?: throw IllegalStateException("No user configured")
    }

    override suspend fun signInWithGoogle(googleIdToken: String): FirebaseUserInfo {
        return signInResult ?: throw IllegalStateException("No user configured")
    }

    override suspend fun signUpWithEmail(email: String, password: String): FirebaseUserInfo {
        return signUpResult ?: throw IllegalStateException("No user configured")
    }
}