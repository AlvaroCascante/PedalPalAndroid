package com.quetoquenana.and.pedalpal.feature.auth.data.repository

import com.quetoquenana.and.pedalpal.feature.auth.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.pedalpal.feature.auth.domain.model.AuthToken
import com.quetoquenana.and.pedalpal.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remote: AuthRemoteDataSource,
) : AuthRepository {
    override suspend fun login(username: String, password: String): AuthToken {
        return remote.login(username = username, password = password)
    }
}