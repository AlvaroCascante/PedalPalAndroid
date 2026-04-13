package com.quetoquenana.and.features.authentication.data.remote.dataSource

import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserRequestDto
import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserResponseDto

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
    ): CreateUserResponseDto

    suspend fun resolveFirebaseSession(firebaseToken: String): CreateUserResponseDto
}
