package com.quetoquenana.and.features.authentication.data.local.datasource

import com.quetoquenana.and.features.authentication.data.local.dao.AuthSessionDao
import com.quetoquenana.and.features.authentication.data.local.entity.AuthSessionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionLocalDataSourceRoom @Inject constructor(
    private val sessionDao: AuthSessionDao
) : SessionLocalDataSource {

    override suspend fun getSession(): AuthSessionEntity? =
        sessionDao.getSession()

    override fun observeActiveSession(): Flow<AuthSessionEntity?> =
        sessionDao.observeSession()

    override suspend fun hasActiveSession(): Boolean =
        sessionDao.hasActiveSession()

    override suspend fun saveSession(session: AuthSessionEntity) {
        sessionDao.upsert(session)
    }

    override suspend fun clearSession() {
        sessionDao.clearSession()
    }
}