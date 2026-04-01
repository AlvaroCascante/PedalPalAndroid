package com.quetoquenana.and.features.announcements.data.remote.dataSource

import com.quetoquenana.and.features.announcements.domain.model.Announcement

interface AnnouncementRemoteDataSource {
    suspend fun getAnnouncements(): List<Announcement>
}
