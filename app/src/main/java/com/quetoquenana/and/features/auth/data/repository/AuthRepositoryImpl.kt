package com.quetoquenana.and.features.auth.data.repository

import com.quetoquenana.and.features.auth.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.features.auth.data.remote.dataSource.FirebaseAuthDataSource
import com.quetoquenana.and.features.auth.domain.model.AuthToken
import com.quetoquenana.and.features.auth.domain.model.BackendCreateUserRequest
import com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo
import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remote: com.quetoquenana.and.features.auth.data.remote.dataSource.AuthRemoteDataSource,
    private val firebase: com.quetoquenana.and.features.auth.data.remote.dataSource.FirebaseAuthDataSource,
) : com.quetoquenana.and.features.auth.domain.repository.AuthRepository {

    override suspend fun createBackendUser(request: com.quetoquenana.and.features.auth.domain.model.BackendCreateUserRequest, firebaseIdToken: String): com.quetoquenana.and.features.auth.domain.model.AuthToken {
        return remote.createUser(request, firebaseIdToken)
    }

    override suspend fun getCurrentUserInfo(): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo? {
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

    override suspend fun signInWithEmail(email: String, password: String): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo {
        return firebase.signInWithEmail(email, password)
    }

    override suspend fun signInWithGoogle(googleIdToken: String): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo {
        return firebase.signInWithGoogle(googleIdToken)
    }

    override suspend fun signUpWithEmail(email: String, password: String): com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo {
        return firebase.signUpWithEmail(email, password)
    }
}