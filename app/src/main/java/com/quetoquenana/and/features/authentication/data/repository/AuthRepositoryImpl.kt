package com.quetoquenana.and.features.authentication.data.repository

import com.quetoquenana.and.core.network.NetworkException
import com.quetoquenana.and.features.authentication.data.local.datasource.AuthUserLocalDataSource
import com.quetoquenana.and.features.authentication.data.local.datasource.SessionLocalDataSource
import com.quetoquenana.and.features.authentication.data.local.datasource.UserCacheLocalDataSource
import com.quetoquenana.and.features.authentication.data.local.entity.AuthSessionEntity
import com.quetoquenana.and.features.authentication.data.local.entity.AuthUserEntity
import com.quetoquenana.and.features.authentication.data.local.entity.toEntity
import com.quetoquenana.and.features.authentication.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.features.authentication.data.remote.dataSource.FirebaseAuthDataSource
import com.quetoquenana.and.features.authentication.data.remote.dto.toDto
import com.quetoquenana.and.features.authentication.data.remote.dto.toResult
import com.quetoquenana.and.features.authentication.domain.model.CreateUserRequest
import com.quetoquenana.and.features.authentication.domain.model.CreateUserResult
import com.quetoquenana.and.features.authentication.domain.model.CreateUserUseCaseResult
import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel
import com.quetoquenana.and.features.authentication.domain.model.SessionStatus
import com.quetoquenana.and.features.authentication.domain.repository.AuthRepository
import com.quetoquenana.and.features.authentication.session.StoredTokens
import com.quetoquenana.and.features.authentication.session.TokenStorage
import com.quetoquenana.and.features.profile.data.local.datasource.ProfileLocalDataSource
import com.quetoquenana.and.features.profile.data.local.entity.toEntity
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val authUserLocalDataSource: AuthUserLocalDataSource,
    private val remote: AuthRemoteDataSource,
    private val firebase: FirebaseAuthDataSource,
    private val tokenStorage: TokenStorage,
    private val userCacheLocalDataSource: UserCacheLocalDataSource,
    private val profileLocalDataSource: ProfileLocalDataSource
) : AuthRepository {

    override suspend fun completeRegistration(request: CreateUserRequest): CreateUserUseCaseResult {
        return try {
            firebase.getCurrentUserInfo() ?: return CreateUserUseCaseResult.InvalidFirebaseSession

            val idToken = firebase.getIdToken(forceRefresh = true)

            val result = remote.completeRegistration(
                request = request.toDto(),
                firebaseToken = idToken
            ).registration.toResult()

            saveSession(result = result)

            CreateUserUseCaseResult.Success(userId = result.user.id)
        } catch (e: NetworkException.Unauthorized) {
            Timber.e(e, "Unauthorized while completing registration")
            CreateUserUseCaseResult.InvalidFirebaseSession
        } catch (e: NetworkException) {
            Timber.e(e, "Network error while completing registration")
            if (e is NetworkException.NoConnection ||
                e is NetworkException.Timeout ||
                e is NetworkException.Transport
            ) {
                CreateUserUseCaseResult.NetworkError
            } else {
                CreateUserUseCaseResult.UnknownError
            }
        } catch (e: IOException) {
            Timber.e(e, "IOException error while completing registration")
            CreateUserUseCaseResult.NetworkError
        } catch (e: Exception) {
            Timber.e(e, "Exception error while completing registration")
            CreateUserUseCaseResult.UnknownError
        }
    }

    override suspend fun restoreSession(): SessionStatus {
        return restoreLocalSession() ?: restoreFirebaseFallback()
    }

    override suspend fun getUserDisplayName(): String? {
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

            saveSession(result = result)

            if (result.user.profileCompleted) {
                SessionStatus.Authenticated
            } else {
                SessionStatus.ProfileCompletionRequired
            }
        } catch (_: NetworkException.NotFound) {
            SessionStatus.ProfileCompletionRequired
        } catch (e: NetworkException) {
            Timber.e(e, "Network exception while resolving backend session")
            SessionStatus.Unauthenticated
        } catch (e: IOException) {
            Timber.e(e, "IOException while resolving backend session")
            SessionStatus.Unauthenticated
        } catch (e: Exception) {
            handleRestoreSessionFailure(e)
        }
    }

    private suspend fun restoreLocalSession(): SessionStatus? {
        val session = sessionLocalDataSource.getSession()
            ?.takeIf(::isValidLocalSession)
            ?: return null

        val user = authUserLocalDataSource.getUser(session.userId) ?: return null

        tokenStorage.saveTokens(
            StoredTokens(
                accessToken = session.accessToken,
                refreshToken = session.refreshToken
            )
        )

        return user.toSessionStatus()
    }

    private fun isValidLocalSession(session: AuthSessionEntity): Boolean {
        return session.accessToken.isNotBlank() &&
            !session.refreshToken.isNullOrBlank()
    }

    private suspend fun restoreFirebaseFallback(): SessionStatus {
        val firebaseUser = try {
            firebase.getCurrentUserInfo()
        } catch (e: Exception) {
            Timber.w(e, "Unable to read Firebase user during session recovery")
            return SessionStatus.Unauthenticated
        } ?: return SessionStatus.Unauthenticated

        return resolveRemoteSession(firebaseUser)
    }

    private fun AuthUserEntity.toSessionStatus(): SessionStatus {
        return if (profileCompleted) {
            SessionStatus.Authenticated
        } else {
            SessionStatus.ProfileCompletionRequired
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
        result: CreateUserResult
    ) {
        val now = System.currentTimeMillis()
        val backendUserId = result.user.id
        authUserLocalDataSource.saveUser(
            result.user.toEntity(
                id = backendUserId,
                currentTimeMillis = now
            )
        )
        sessionLocalDataSource.saveSession(
            result.session.toEntity(
                userId = backendUserId,
                currentTimeMillis = now
            )
        )
        tokenStorage.saveTokens(
            StoredTokens(
                accessToken = result.session.accessToken,
                refreshToken = result.session.refreshToken
            )
        )
        profileLocalDataSource.saveProfile(
            profile = result.user.toEntity(id = backendUserId)
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
        tokenStorage.clear()
        userCacheLocalDataSource.clearUserRideData()
    }


    private fun Throwable.hasCauseMessage(value: String): Boolean {
        return generateSequence(this as Throwable?) { it.cause }
            .any { throwable ->
                throwable.message?.contains(value, ignoreCase = true) == true
            }
    }
}
