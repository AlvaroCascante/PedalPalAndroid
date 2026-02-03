package com.quetoquenana.and.pedalpal.features.landing.data.repository

import com.quetoquenana.and.pedalpal.features.landing.data.remote.dataSource.LandingRemoteDataSource
import com.quetoquenana.and.pedalpal.features.landing.domain.model.LandingPageItem
import com.quetoquenana.and.pedalpal.features.landing.domain.repository.LandingRepository
import javax.inject.Inject

class LandingRepositoryImpl @Inject constructor(
    private val remote: LandingRemoteDataSource
) : LandingRepository {
    override suspend fun getLandingItems(): List<LandingPageItem> = remote.getLandingItems()
}
