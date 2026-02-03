package com.quetoquenana.and.pedalpal.features.home.ui

import com.quetoquenana.and.pedalpal.features.appointments.domain.model.Appointment
import com.quetoquenana.and.pedalpal.features.suggestions.domain.model.Suggestion
import com.quetoquenana.and.pedalpal.features.landing.domain.model.LandingPageItem

data class HomeUiState(
    val appointments: List<Appointment> = emptyList(),
    val suggestions: List<Suggestion> = emptyList(),
    val landingItems: List<LandingPageItem> = emptyList(),
    val isLoading: Boolean = false
)