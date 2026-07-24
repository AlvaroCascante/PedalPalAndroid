package com.quetoquenana.and.features.announcements.data.remote.dataSource

import com.quetoquenana.and.core.network.networkCall
import com.quetoquenana.and.features.announcements.data.remote.api.AnnouncementApi
import com.quetoquenana.and.features.announcements.data.remote.dto.toDomain
import com.quetoquenana.and.features.announcements.domain.model.Announcement
import javax.inject.Inject

class AnnouncementRemoteDataSourceRetrofit @Inject constructor(
    private val api: AnnouncementApi
) : AnnouncementRemoteDataSource {

    override suspend fun getAnnouncements(): List<Announcement> {
        return networkCall {
            api.getActiveAnnouncements()
        }.map { it.toDomain() }
            .sortedWith(comparator = compareBy(comparator = nullsLast()) { it.position })
    }
}
