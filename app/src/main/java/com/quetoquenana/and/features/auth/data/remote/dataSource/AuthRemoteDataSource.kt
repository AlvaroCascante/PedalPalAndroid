package com.quetoquenana.and.features.auth.data.remote.dataSource

import com.quetoquenana.and.features.auth.data.remote.dto.request.CreateUserRequestDto
import com.quetoquenana.and.features.auth.data.remote.dto.response.CreateUserResponse

/**
 * Remote data source to call your authentication backend.
 *
 * Platform implementations live in:
 * - shared/src/androidMain/... (Android)
 * - shared/src/iosMain/... (iOS)
 */
interface AuthRemoteDataSource {
    suspend fun completeRegistration(
        request: CreateUserRequestDto,
        firebaseToken: String
    ): CreateUserResponse
}