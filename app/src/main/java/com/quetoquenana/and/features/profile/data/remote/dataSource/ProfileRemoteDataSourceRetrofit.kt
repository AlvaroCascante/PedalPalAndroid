package com.quetoquenana.and.features.profile.data.remote.dataSource

import com.quetoquenana.and.features.profile.data.remote.api.ProfileApi
import com.quetoquenana.and.features.profile.data.remote.dto.ProfileResponseDto
import java.util.UUID
import javax.inject.Inject

class ProfileRemoteDataSourceRetrofit @Inject constructor(
    private val profileApi: ProfileApi,
) : ProfileRemoteDataSource {

    override suspend fun getProfile(userId: UUID): ProfileResponseDto {
        return profileApi.getProfile(id = userId).data
    }
}


