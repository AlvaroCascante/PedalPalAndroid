package com.quetoquenana.and.features.authentication.data.local.datasource

import com.quetoquenana.and.features.authentication.data.local.entity.AuthSessionEntity
import kotlinx.coroutines.flow.Flow

interface SessionLocalDataSource {
    suspend fun getSession(): AuthSessionEntity?
    fun observeActiveSession(): Flow<AuthSessionEntity?>
    suspend fun hasActiveSession(): Boolean
    suspend fun saveSession(session: AuthSessionEntity)
    suspend fun clearSession()
}