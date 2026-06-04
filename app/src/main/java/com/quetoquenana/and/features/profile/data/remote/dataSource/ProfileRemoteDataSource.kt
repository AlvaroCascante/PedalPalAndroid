package com.quetoquenana.and.features.profile.data.remote.dataSource

import com.quetoquenana.and.features.profile.data.remote.dto.ProfileResponseDto
import java.util.UUID

interface ProfileRemoteDataSource {
    suspend fun getProfile(userId: UUID): ProfileResponseDto
}

