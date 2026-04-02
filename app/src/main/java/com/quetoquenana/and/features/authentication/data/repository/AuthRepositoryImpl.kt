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
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val authUserLocalDataSource: AuthUserLocalDataSource,
    private val remote: AuthRemoteDataSource,
    private val firebase: FirebaseAuthDataSource,
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

            val resultUser = result.user
            val resultUSession = result.session
            val now = System.currentTimeMillis()

            authUserLocalDataSource.saveUser(resultUser.toEntity(id = firebaseUser.uid, currentTimeMillis = now))
            sessionLocalDataSource.saveSession(resultUSession.toEntity(userId = firebaseUser.uid, currentTimeMillis = now))

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

    override suspend fun hasActiveSession(): Boolean {
        return sessionLocalDataSource.hasActiveSession()
    }

    override suspend fun restoreSession(): SessionStatus {
        val session = sessionLocalDataSource.getSession()
            ?: return SessionStatus.Unauthenticated

        if (!session.isLoggedIn) {
            return SessionStatus.Unauthenticated
        }

        val user = authUserLocalDataSource.getUser(session.userId)
            ?: return SessionStatus.Unauthenticated

        return if (user.profileCompleted) {
            SessionStatus.Authenticated
        } else {
            SessionStatus.ProfileCompletionRequired
        }
    }

    override suspend fun logout() {
        sessionLocalDataSource.clearSession()
        authUserLocalDataSource.clearUsers()
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
}