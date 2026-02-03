package com.quetoquenana.and.pedalpal.feature.home.ui

import com.quetoquenana.and.pedalpal.feature.appointments.domain.model.Appointment
import com.quetoquenana.and.pedalpal.feature.suggestions.domain.model.Suggestion
import com.quetoquenana.and.pedalpal.feature.landing.domain.model.LandingPageItem

data class HomeUiState(
    val appointments: List<Appointment> = emptyList(),
    val suggestions: List<Suggestion> = emptyList(),
    val landingItems: List<LandingPageItem> = emptyList(),
    val isLoading: Boolean = false
)