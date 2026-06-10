package com.quetoquenana.and.features.profile.data.local.datasource

import com.quetoquenana.and.features.profile.data.local.dao.ProfileDao
import com.quetoquenana.and.features.profile.data.local.entity.ProfileEntity
import java.util.UUID
import javax.inject.Inject

class ProfileLocalDataSourceRoom @Inject constructor(
    private val profileDao: ProfileDao,
) : ProfileLocalDataSource {

    override suspend fun getCurrentProfile(userId: UUID): Result<ProfileEntity> {
        return try {
            val entity = profileDao.getCurrentProfile(userId)
            if (entity != null) {
                Result.success(value = entity)
            } else {
                Result.failure(exception = NoSuchElementException("No profile found for userId=$userId"))
            }
        } catch (t: Throwable) {
            Result.failure(exception = t)
        }
    }

    override suspend fun saveProfile(profile: ProfileEntity) {
        profileDao.upsert(profile)
    }

    override suspend fun clearProfiles() {
        profileDao.deleteAll()
    }
}

