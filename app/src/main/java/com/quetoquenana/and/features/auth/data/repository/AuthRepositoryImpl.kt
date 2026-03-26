package com.quetoquenana.and.features.auth.data.repository

import com.quetoquenana.and.features.auth.data.local.datasource.AuthUserLocalDataSource
import com.quetoquenana.and.features.auth.data.local.datasource.SessionLocalDataSource
import com.quetoquenana.and.features.auth.data.local.entity.toEntity
import com.quetoquenana.and.features.auth.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.features.auth.data.remote.dataSource.FirebaseAuthDataSource
import com.quetoquenana.and.features.auth.data.remote.dto.request.toDto
import com.quetoquenana.and.features.auth.data.remote.dto.response.toResult
import com.quetoquenana.and.features.auth.domain.model.CreateUserRequest
import com.quetoquenana.and.features.auth.domain.model.CreateUserUseCaseResult
import com.quetoquenana.and.features.auth.domain.model.FirebaseUserModel
import com.quetoquenana.and.features.auth.domain.model.SessionStatus
import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import retrofit2.HttpException
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
            ).toResult()

            val resultUser = result.user
            val resultUSession = result.session
            val now = System.currentTimeMillis()

            authUserLocalDataSource.saveUser(resultUser.toEntity(id = firebaseUser.uid, currentTimeMillis = now))
            sessionLocalDataSource.saveSession(resultUSession.toEntity(userId = firebaseUser.uid, currentTimeMillis = now))

            CreateUserUseCaseResult.Success(userId = firebaseUser.uid)
        } catch (e: IOException) {
            CreateUserUseCaseResult.NetworkError

        } catch (e: HttpException) {
            if (e.code() == 401) {
                CreateUserUseCaseResult.InvalidFirebaseSession
            } else {
                CreateUserUseCaseResult.UnknownError
            }

        } catch (e: Exception) {
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