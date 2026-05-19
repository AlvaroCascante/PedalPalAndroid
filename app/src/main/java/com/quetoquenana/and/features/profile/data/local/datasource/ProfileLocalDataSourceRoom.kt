package com.quetoquenana.and.features.profile.data.local.datasource

import com.quetoquenana.and.features.profile.data.local.dao.ProfileDao
import com.quetoquenana.and.features.profile.data.local.entity.ProfileEntity
import javax.inject.Inject

class ProfileLocalDataSourceRoom @Inject constructor(
    private val profileDao: ProfileDao,
) : ProfileLocalDataSource {

    override suspend fun getProfile(userId: String): ProfileEntity? = profileDao.getById(userId)

    override suspend fun saveProfile(profile: ProfileEntity) {
        profileDao.upsert(profile)
    }

    override suspend fun clearProfiles() {
        profileDao.deleteAll()
    }
}

