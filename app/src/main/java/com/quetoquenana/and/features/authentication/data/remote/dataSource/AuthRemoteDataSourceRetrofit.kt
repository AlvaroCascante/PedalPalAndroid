package com.quetoquenana.and.features.authentication.data.remote.dataSource

import com.quetoquenana.and.features.authentication.data.remote.api.AuthApi
import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserRequestDto
import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserResponseDto
import javax.inject.Inject

/**
 * Ktor-based implementation that can live in commonMain.
 *
 * NOTE: endpoint paths/baseUrl are placeholders. Wire them to your real backend later.
 */
class AuthRemoteDataSourceRetrofit @Inject constructor(
    private val authApi: AuthApi
) : AuthRemoteDataSource {

    override suspend fun completeRegistration(
        request: CreateUserRequestDto,
        firebaseToken: String
    ): CreateUserResponseDto {
        val response = authApi.completeRegistrationFromFirebase(
            request = request,
            authorization = "Bearer $firebaseToken"
        )
        return response.data.registration
    }
}