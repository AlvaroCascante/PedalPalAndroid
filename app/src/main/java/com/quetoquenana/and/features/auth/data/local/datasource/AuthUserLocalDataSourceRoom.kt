package com.quetoquenana.and.features.auth.data.local.datasource

import com.quetoquenana.and.features.auth.data.local.dao.AuthUserDao
import com.quetoquenana.and.features.auth.data.local.entity.AuthUserEntity
import javax.inject.Inject

class AuthUserLocalDataSourceRoom @Inject constructor(
    private val userDao: AuthUserDao
) : AuthUserLocalDataSource {

    override suspend fun getUser(userId: String): AuthUserEntity? =
        userDao.getById(userId)

    override suspend fun saveUser(user: AuthUserEntity) {
        userDao.upsert(user)
    }

    override suspend fun clearUsers() {
        userDao.deleteAll()
    }
}