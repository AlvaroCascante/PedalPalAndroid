package com.quetoquenana.and.pedalpal.features.login.data.repository

import com.quetoquenana.and.pedalpal.features.login.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.pedalpal.features.login.data.remote.dataSource.FirebaseAuthDataSource
import com.quetoquenana.and.pedalpal.features.login.domain.model.AuthToken
import com.quetoquenana.and.pedalpal.features.login.domain.model.BackendCreateUserRequest
import com.quetoquenana.and.pedalpal.features.login.domain.model.FirebaseUserInfo
import com.quetoquenana.and.pedalpal.features.login.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remote: AuthRemoteDataSource,
    private val firebase: FirebaseAuthDataSource,
) : AuthRepository {

    override suspend fun createBackendUser(request: BackendCreateUserRequest, firebaseIdToken: String): AuthToken {
        return remote.createUser(request, firebaseIdToken)
    }

    override suspend fun getCurrentUserInfo(): FirebaseUserInfo? {
        return firebase.getCurrentUserInfo()
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

    override suspend fun signInWithEmail(email: String, password: String): FirebaseUserInfo {
        return firebase.signInWithEmail(email, password)
    }

    override suspend fun signInWithGoogle(googleIdToken: String): FirebaseUserInfo {
        return firebase.signInWithGoogle(googleIdToken)
    }

    override suspend fun signUpWithEmail(email: String, password: String): FirebaseUserInfo {
        return firebase.signUpWithEmail(email, password)
    }
}