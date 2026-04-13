package com.quetoquenana.and.auth.domain.repository

import com.quetoquenana.and.features.authentication.domain.model.CreateUserRequest
import com.quetoquenana.and.features.authentication.domain.model.CreateUserUseCaseResult
import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel
import com.quetoquenana.and.features.authentication.domain.model.SessionStatus
import com.quetoquenana.and.features.authentication.domain.repository.AuthRepository

/**
 * Reusable fake AuthRepository for unit tests. Configure returned values via constructor.
 */
class FakeAuthRepository(
    private val signInResult: FirebaseUserModel? = null,
    private val signInException: Throwable? = null,
    private val signUpResult: FirebaseUserModel? = null,
    private val hasActiveSessionResult: Boolean = false,
    private val sessionStatus: SessionStatus = SessionStatus.Unauthenticated
) : AuthRepository {
    var sendEmailVerificationCalled = false
    var completeRegistrationCalledWith: CreateUserRequest? = null
    var reloadUserCalled = false
    var restoreSessionCalled = false
    var logoutCalled = false

    override suspend fun completeRegistration(request: CreateUserRequest): CreateUserUseCaseResult {
        completeRegistrationCalledWith = request
        return CreateUserUseCaseResult.Success(userId = "fake-user-id")
    }

    override suspend fun getFirebaseIdToken(forceRefresh: Boolean): String = "fake-token"

    override suspend fun isEmailVerified(): Boolean = signInResult?.isEmailVerified ?: false

    override suspend fun reloadUser() {
        reloadUserCalled = true
    }

    override suspend fun sendEmailVerification() {
        sendEmailVerificationCalled = true
    }

    override suspend fun signInWithEmail(email: String, password: String): FirebaseUserModel {
        signInException?.let { throw it }
        return signInResult ?: throw IllegalStateException("No user configured")
    }

    override suspend fun signInWithGoogle(googleIdToken: String): FirebaseUserModel {
        return signInResult ?: throw IllegalStateException("No user configured")
    }

    override suspend fun signUpWithEmail(email: String, password: String): FirebaseUserModel {
        return signUpResult ?: throw IllegalStateException("No user configured")
    }

    override suspend fun hasActiveSession(): Boolean = hasActiveSessionResult

    override suspend fun restoreSession(): SessionStatus {
        restoreSessionCalled = true
        return sessionStatus
    }

    override suspend fun logout() {
        logoutCalled = true
    }
}
