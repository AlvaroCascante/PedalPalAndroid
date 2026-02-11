package com.quetoquenana.and.features.auth.data.remote.dataSource

/**
 * Remote data source to call your authentication backend.
 *
 * Platform implementations live in:
 * - shared/src/androidMain/... (Android)
 * - shared/src/iosMain/... (iOS)
 */
interface AuthRemoteDataSource {
    suspend fun createUser(request: com.quetoquenana.and.features.auth.domain.model.BackendCreateUserRequest, firebaseIdToken: String): com.quetoquenana.and.features.auth.domain.model.AuthToken
}