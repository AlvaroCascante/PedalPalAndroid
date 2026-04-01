package com.quetoquenana.and.features.home.ui

import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion

data class HomeUiState(
    val announcements: List<Announcement> = emptyList(),
    val bikes: List<Bike> = emptyList(),
    val headerSection: HeaderSection = HeaderSection.Loading,
    val isLoading: Boolean = false
)

sealed interface HeaderSection {
    data object Loading: HeaderSection

    data class NoBikes(
        val createBikeOption: Boolean = true
    ) : HeaderSection

    data class Content(
        val appointments: List<Appointment> = emptyList(),
        val suggestions: List<Suggestion> = emptyList(),
    ) : HeaderSection
}
