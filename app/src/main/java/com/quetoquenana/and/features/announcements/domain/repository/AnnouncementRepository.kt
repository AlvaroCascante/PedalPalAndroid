package com.quetoquenana.and.features.announcements.domain.repository

import com.quetoquenana.and.features.announcements.domain.model.Announcement

interface AnnouncementRepository {
    suspend fun getAnnouncements(): List<Announcement>
}
