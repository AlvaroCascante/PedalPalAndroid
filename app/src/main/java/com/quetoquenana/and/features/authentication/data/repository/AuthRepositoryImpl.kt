package com.quetoquenana.and.features.authentication.data.repository

import com.quetoquenana.and.features.authentication.data.local.datasource.AuthUserLocalDataSource
import com.quetoquenana.and.features.authentication.data.local.datasource.SessionLocalDataSource
import com.quetoquenana.and.features.authentication.data.local.entity.toEntity
import com.quetoquenana.and.features.authentication.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.features.authentication.data.remote.dataSource.FirebaseAuthDataSource
import com.quetoquenana.and.features.authentication.data.remote.dto.toDto
import com.quetoquenana.and.features.authentication.data.remote.dto.toResult
import com.quetoquenana.and.features.authentication.domain.model.CreateUserRequest
import com.quetoquenana.and.features.authentication.domain.model.CreateUserUseCaseResult
import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel
import com.quetoquenana.and.features.authentication.domain.model.SessionStatus
import com.quetoquenana.and.features.authentication.domain.repository.AuthRepository
import com.quetoquenana.and.features.authentication.session.StoredTokens
import com.quetoquenana.and.features.authentication.session.TokenStorage
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val authUserLocalDataSource: AuthUserLocalDataSource,
    private val remote: AuthRemoteDataSource,
    private val firebase: FirebaseAuthDataSource,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override suspend fun completeRegistration(request: CreateUserRequest): CreateUserUseCaseResult {
        return try {
            val firebaseUser = firebase.getCurrentUserInfo()
                ?: return CreateUserUseCaseResult.InvalidFirebaseSession

            val idToken = firebase.getIdToken(forceRefresh = true)

            val result = remote.completeRegistration(
                request = request.toDto(),
                firebaseToken = idToken
            ).registration.toResult()

            saveSession(firebaseUid = firebaseUser.uid, result = result)

            CreateUserUseCaseResult.Success(userId = firebaseUser.uid)
        } catch (e: IOException) {
            Timber.e(e, "IOException error while completing registration")
            CreateUserUseCaseResult.NetworkError
        } catch (e: HttpException) {
            Timber.e(e, "HttpException error while completing registration")
            if (e.code() == 401) {
                CreateUserUseCaseResult.InvalidFirebaseSession
            } else {
                CreateUserUseCaseResult.UnknownError
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception error while completing registration")
            CreateUserUseCaseResult.UnknownError
        }
    }

    override suspend fun restoreSession(): SessionStatus {
        val session = sessionLocalDataSource.getSession()
        if (session?.isLoggedIn == true) {
            val user = authUserLocalDataSource.getUser(session.userId)
            if (user != null) {
                tokenStorage.saveTokens(
                    StoredTokens(
                        accessToken = session.accessToken,
                        refreshToken = session.refreshToken
                    )
                )
                return if (user.profileCompleted) {
                    SessionStatus.Authenticated
                } else {
                    SessionStatus.ProfileCompletionRequired
                }
            }
        }

        val firebaseUser = try {
            firebase.getCurrentUserInfo()
        } catch (e: Exception) {
            return handleRestoreSessionFailure(e)
        } ?: return SessionStatus.Unauthenticated

        return resolveRemoteSession(firebaseUser)
    }

    override suspend fun getCurrentUserDisplayName(): String? {
        val session = sessionLocalDataSource.getSession()?.takeIf { it.isLoggedIn } ?: return null
        return authUserLocalDataSource.getUser(session.userId)
            ?.name
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }

    private suspend fun resolveRemoteSession(firebaseUser: FirebaseUserModel): SessionStatus {
        if (!firebaseUser.isEmailVerified) {
            return SessionStatus.Unauthenticated
        }

        return try {
            val idToken = firebase.getIdToken(forceRefresh = true)
            val result = remote.resolveFirebaseSession(firebaseToken = idToken)
                .registration
                .toResult()
            saveSession(firebaseUid = firebaseUser.uid, result = result)

            if (result.user.profileCompleted) {
                SessionStatus.Authenticated
            } else {
                SessionStatus.ProfileCompletionRequired
            }
        } catch (e: HttpException) {
            if (e.code() == 404) {
                SessionStatus.ProfileCompletionRequired
            } else {
                Timber.e(e, "HttpException while resolving backend session")
                SessionStatus.Unauthenticated
            }
        } catch (e: IOException) {
            Timber.e(e, "IOException while resolving backend session")
            SessionStatus.Unauthenticated
        } catch (e: Exception) {
            handleRestoreSessionFailure(e)
        }
    }

    private suspend fun handleRestoreSessionFailure(error: Exception): SessionStatus {
        return if (error.hasCauseMessage("INVALID_REFRESH_TOKEN")) {
            Timber.w(error, "Invalid Firebase refresh token detected. Clearing stored auth state.")
            clearPersistedAuthState()
            SessionStatus.Unauthenticated
        } else {
            Timber.e(error, "Exception while restoring session")
            SessionStatus.Unauthenticated
        }
    }

    private suspend fun saveSession(
        firebaseUid: String,
        result: com.quetoquenana.and.features.authentication.domain.model.CreateUserResult
    ) {
        val now = System.currentTimeMillis()
        authUserLocalDataSource.saveUser(
            result.user.toEntity(
                id = firebaseUid,
                currentTimeMillis = now
            )
        )
        sessionLocalDataSource.saveSession(
            result.session.toEntity(
                userId = firebaseUid,
                currentTimeMillis = now
            )
        )
        tokenStorage.saveTokens(
            StoredTokens(
                accessToken = result.session.accessToken,
                refreshToken = result.session.refreshToken
            )
        )
    }

    override suspend fun hasActiveSession(): Boolean {
        return sessionLocalDataSource.hasActiveSession()
    }

    override suspend fun logout() {
        firebase.signOut()
        clearPersistedAuthState()
    }

    override suspend fun getFirebaseIdToken(forceRefresh: Boolean): String {
        return firebase.getIdToken(forceRefresh)
    }

    override suspend fun isEmailVerified(): Boolean {
        return firebase.isEmailVerified()
    }

    override suspend fun reloadUser() {
        firebase.reloadUser()
    }

    override suspend fun sendEmailVerification() {
        firebase.sendEmailVerification()
    }

    override suspend fun signInWithEmail(email: String, password: String): FirebaseUserModel {
        return firebase.signInWithEmail(email, password)
    }

    override suspend fun signInWithGoogle(googleIdToken: String): FirebaseUserModel {
        return firebase.signInWithGoogle(googleIdToken)
    }

    override suspend fun signUpWithEmail(email: String, password: String): FirebaseUserModel {
        return firebase.signUpWithEmail(email, password)
    }

    private suspend fun clearPersistedAuthState() {
        sessionLocalDataSource.clearSession()
        authUserLocalDataSource.clearUsers()
        tokenStorage.clear()
    }

    private fun Throwable.hasCauseMessage(value: String): Boolean {
        return generateSequence(this as Throwable?) { it.cause }
            .any { throwable ->
                throwable.message?.contains(value, ignoreCase = true) == true
            }
    }
}
