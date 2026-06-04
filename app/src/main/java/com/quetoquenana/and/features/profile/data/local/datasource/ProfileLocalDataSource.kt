package com.quetoquenana.and.features.profile.data.local.datasource

import com.quetoquenana.and.features.profile.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProfileLocalDataSource {
    fun observeProfile(userId: UUID): Flow<ProfileEntity>
    suspend fun saveProfile(profile: ProfileEntity)
    suspend fun clearProfiles()
}

