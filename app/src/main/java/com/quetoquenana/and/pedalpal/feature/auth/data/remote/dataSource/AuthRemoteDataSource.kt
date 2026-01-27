package com.quetoquenana.and.pedalpal.feature.auth.data.remote.dataSource

import com.quetoquenana.and.pedalpal.feature.auth.domain.model.AuthToken

/**
 * Remote data source to call your authentication backend.
 *
 * Platform implementations live in:
 * - shared/src/androidMain/... (Android)
 * - shared/src/iosMain/... (iOS)
 */
interface AuthRemoteDataSource {
    suspend fun login(username: String, password: String): AuthToken
}