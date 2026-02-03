package com.quetoquenana.and.pedalpal.feature.landing.data.repository

import com.quetoquenana.and.pedalpal.feature.landing.data.remote.dataSource.LandingRemoteDataSource
import com.quetoquenana.and.pedalpal.feature.landing.domain.model.LandingPageItem
import com.quetoquenana.and.pedalpal.feature.landing.domain.repository.LandingRepository
import javax.inject.Inject

class LandingRepositoryImpl @Inject constructor(
    private val remote: LandingRemoteDataSource
) : LandingRepository {
    override suspend fun getLandingItems(): List<LandingPageItem> = remote.getLandingItems()
}
