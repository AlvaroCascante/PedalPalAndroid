package com.quetoquenana.and.features.home.domain.model

import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion

data class HomeContent(
    val appointments: List<Appointment>,
    val announcements: List<Announcement>,
    val suggestions: List<Suggestion>,
    val bikes: List<Bike>
)