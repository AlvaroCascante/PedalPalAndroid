package com.quetoquenana.and.features.profile.data.local.datasource

import com.quetoquenana.and.features.profile.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

interface ProfileLocalDataSource {
    fun observeProfile(userId: String): Flow<ProfileEntity>
    suspend fun saveProfile(profile: ProfileEntity)
    suspend fun clearProfiles()
}

