package com.quetoquenana.and.features.authentication.data.local.datasource

import com.quetoquenana.and.features.authentication.data.local.entity.AuthUserEntity

interface AuthUserLocalDataSource {
    suspend fun getUser(userId: String): AuthUserEntity?

    suspend fun getUserByEmail(email: String): AuthUserEntity?
    suspend fun saveUser(user: AuthUserEntity)
    suspend fun clearUsers()
}