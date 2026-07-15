package com.quetoquenana.and.features.home.domain.fakes

import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.announcements.domain.repository.AnnouncementRepository

class FakeAnnouncementRepository(
    private val announcements: List<Announcement> = emptyList()
) : AnnouncementRepository {
    override suspend fun getAnnouncements(): List<Announcement> = announcements
}
