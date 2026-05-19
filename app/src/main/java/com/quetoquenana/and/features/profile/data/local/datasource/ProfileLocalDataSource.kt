package com.quetoquenana.and.features.profile.data.local.datasource

import com.quetoquenana.and.features.profile.data.local.entity.ProfileEntity

interface ProfileLocalDataSource {
    suspend fun getProfile(userId: String): ProfileEntity?
    suspend fun saveProfile(profile: ProfileEntity)
    suspend fun clearProfiles()
}

