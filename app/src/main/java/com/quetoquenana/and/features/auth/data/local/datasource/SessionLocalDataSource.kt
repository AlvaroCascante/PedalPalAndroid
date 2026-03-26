package com.quetoquenana.and.features.auth.data.local.datasource

import com.quetoquenana.and.features.auth.data.local.entity.AuthSessionEntity

interface SessionLocalDataSource {
    suspend fun getSession(): AuthSessionEntity?
    suspend fun hasActiveSession(): Boolean
    suspend fun saveSession(session: AuthSessionEntity)
    suspend fun clearSession()
}