package com.quetoquenana.and.features.profile.data.local.datasource

import com.quetoquenana.and.features.profile.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProfileLocalDataSource {
    suspend fun getCurrentProfile(userId: UUID): Result<ProfileEntity>
    suspend fun saveProfile(profile: ProfileEntity)
    suspend fun clearProfiles()
}

