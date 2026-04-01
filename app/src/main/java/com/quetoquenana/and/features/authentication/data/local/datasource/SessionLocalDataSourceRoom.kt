package com.quetoquenana.and.features.authentication.data.local.datasource

import com.quetoquenana.and.features.authentication.data.local.dao.AuthSessionDao
import com.quetoquenana.and.features.authentication.data.local.entity.AuthSessionEntity
import javax.inject.Inject

class SessionLocalDataSourceRoom @Inject constructor(
    private val sessionDao: AuthSessionDao
) : SessionLocalDataSource {

    override suspend fun getSession(): AuthSessionEntity? =
        sessionDao.getSession()

    override suspend fun hasActiveSession(): Boolean =
        sessionDao.hasActiveSession()

    override suspend fun saveSession(session: AuthSessionEntity) {
        sessionDao.upsert(session)
    }

    override suspend fun clearSession() {
        sessionDao.clearSession()
    }
}