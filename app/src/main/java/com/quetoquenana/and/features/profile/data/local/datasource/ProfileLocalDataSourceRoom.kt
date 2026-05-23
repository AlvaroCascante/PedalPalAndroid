package com.quetoquenana.and.features.profile.data.local.datasource

import com.quetoquenana.and.features.profile.data.local.dao.ProfileDao
import com.quetoquenana.and.features.profile.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileLocalDataSourceRoom @Inject constructor(
    private val profileDao: ProfileDao,
) : ProfileLocalDataSource {

    override fun observeProfile(userId: String): Flow<ProfileEntity> = profileDao.observeProfile(userId)

    override suspend fun saveProfile(profile: ProfileEntity) {
        profileDao.upsert(profile)
    }

    override suspend fun clearProfiles() {
        profileDao.deleteAll()
    }
}

