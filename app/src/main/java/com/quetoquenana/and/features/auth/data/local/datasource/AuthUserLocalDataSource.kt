package com.quetoquenana.and.features.auth.data.local.datasource

import com.quetoquenana.and.features.auth.data.local.entity.AuthUserEntity

interface AuthUserLocalDataSource {
    suspend fun getUser(userId: String): AuthUserEntity?
    suspend fun saveUser(user: AuthUserEntity)
    suspend fun clearUsers()
}