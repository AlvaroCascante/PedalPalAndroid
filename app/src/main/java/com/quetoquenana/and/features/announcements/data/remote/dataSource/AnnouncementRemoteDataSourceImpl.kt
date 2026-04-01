package com.quetoquenana.and.features.announcements.data.remote.dataSource

import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.R
import javax.inject.Inject

class AnnouncementRemoteDataSourceImpl @Inject constructor() : AnnouncementRemoteDataSource {
    override suspend fun getAnnouncements(): List<Announcement> = listOf(
        Announcement(
            id = "l1",
            title = "Welcome to PedalPal",
            description = "Discover features and get started",
            thumbnailRes = R.drawable.mobi_bike_logo
        ),
        Announcement(
            id = "l2",
            title = "Get a Tune-up",
            description = "Book your maintenance appointment",
            thumbnailRes = null
        ),
        Announcement(
            id = "l3",
            title = "Join Loyalty",
            description = "Earn points with every service",
            thumbnailRes = null
        )
    )
}
