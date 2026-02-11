package com.quetoquenana.and.features.landing.data.repository

import com.quetoquenana.and.features.landing.data.remote.dataSource.LandingRemoteDataSource
import com.quetoquenana.and.features.landing.domain.model.LandingPageItem
import com.quetoquenana.and.features.landing.domain.repository.LandingRepository
import javax.inject.Inject

class LandingRepositoryImpl @Inject constructor(
    private val remote: com.quetoquenana.and.features.landing.data.remote.dataSource.LandingRemoteDataSource
) : com.quetoquenana.and.features.landing.domain.repository.LandingRepository {
    override suspend fun getLandingItems(): List<com.quetoquenana.and.features.landing.domain.model.LandingPageItem> = remote.getLandingItems()
}
