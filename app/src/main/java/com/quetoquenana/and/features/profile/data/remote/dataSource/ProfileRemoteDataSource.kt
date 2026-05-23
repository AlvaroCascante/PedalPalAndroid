package com.quetoquenana.and.features.profile.data.remote.dataSource

import com.quetoquenana.and.features.profile.data.remote.dto.ProfileResponseDto

interface ProfileRemoteDataSource {
    suspend fun getProfile(userId: String): ProfileResponseDto
}

