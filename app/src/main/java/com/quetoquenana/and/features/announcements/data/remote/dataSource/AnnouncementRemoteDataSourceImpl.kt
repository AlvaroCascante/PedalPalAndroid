package com.quetoquenana.and.features.announcements.data.remote.dataSource

import com.quetoquenana.and.features.announcements.data.remote.api.AnnouncementApi
import com.quetoquenana.and.features.announcements.data.remote.dto.toDomain
import com.quetoquenana.and.features.announcements.domain.model.Announcement
import javax.inject.Inject

class AnnouncementRemoteDataSourceImpl @Inject constructor(
    private val api: AnnouncementApi
) : AnnouncementRemoteDataSource {

    override suspend fun getAnnouncements(): List<Announcement> {
        return api.getActiveAnnouncements()
            .data
            .map { it.toDomain() }
            .sortedWith(compareBy(nullsLast()) { it.position })
    }
}
