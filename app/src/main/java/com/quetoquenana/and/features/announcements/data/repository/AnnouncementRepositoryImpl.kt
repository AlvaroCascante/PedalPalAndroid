package com.quetoquenana.and.features.announcements.data.repository

import com.quetoquenana.and.features.announcements.data.remote.dataSource.AnnouncementRemoteDataSource
import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.announcements.domain.repository.AnnouncementRepository
import javax.inject.Inject

// The injection for this repository is provided in the DomainModule,
// because this is used in the home screen module.
class AnnouncementRepositoryImpl @Inject constructor(
    private val remote: AnnouncementRemoteDataSource
) : AnnouncementRepository {
    override suspend fun getAnnouncements(): List<Announcement> {
        return remote.getAnnouncements()
    }
}
