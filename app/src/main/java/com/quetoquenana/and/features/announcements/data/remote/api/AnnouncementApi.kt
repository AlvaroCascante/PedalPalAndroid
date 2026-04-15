package com.quetoquenana.and.features.announcements.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.announcements.data.remote.dto.AnnouncementResponseDto
import retrofit2.http.GET

interface AnnouncementApi {

    @GET("announcements/active")
    suspend fun getActiveAnnouncements(): ApiResponse<List<AnnouncementResponseDto>>
}
