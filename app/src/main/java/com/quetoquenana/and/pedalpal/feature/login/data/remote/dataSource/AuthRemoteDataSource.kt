package com.quetoquenana.and.pedalpal.feature.login.data.remote.dataSource

import com.quetoquenana.and.pedalpal.feature.login.domain.model.AuthToken
import com.quetoquenana.and.pedalpal.feature.login.domain.model.BackendCreateUserRequest

/**
 * Remote data source to call your authentication backend.
 *
 * Platform implementations live in:
 * - shared/src/androidMain/... (Android)
 * - shared/src/iosMain/... (iOS)
 */
interface AuthRemoteDataSource {
    suspend fun createUser(request: BackendCreateUserRequest, firebaseIdToken: String): AuthToken
}