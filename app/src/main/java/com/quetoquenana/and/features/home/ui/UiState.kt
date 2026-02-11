package com.quetoquenana.and.features.home.ui

import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import com.quetoquenana.and.features.landing.domain.model.LandingPageItem

data class HomeUiState(
    val appointments: List<com.quetoquenana.and.features.appointments.domain.model.Appointment> = emptyList(),
    val suggestions: List<com.quetoquenana.and.features.suggestions.domain.model.Suggestion> = emptyList(),
    val landingItems: List<com.quetoquenana.and.features.landing.domain.model.LandingPageItem> = emptyList(),
    val isLoading: Boolean = false
)