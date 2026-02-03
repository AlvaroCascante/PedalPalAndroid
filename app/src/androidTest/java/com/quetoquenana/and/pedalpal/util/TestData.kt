package com.quetoquenana.and.pedalpal.util

import com.quetoquenana.and.pedalpal.features.appointments.domain.model.Appointment
import com.quetoquenana.and.pedalpal.features.suggestions.domain.model.Suggestion

val sampleAppointments = listOf(
    Appointment(id = "1", dateText = "Mon, Feb 12 · 09:00", bikeId = "b1", bikeName = "Trek Domane", thumbnailRes = null),
    Appointment(id = "2", dateText = "Fri, Feb 23 · 15:30", bikeId = "b2", bikeName = "Giant Talon", thumbnailRes = null)
)

val sampleSuggestions = listOf(
    Suggestion(id = "s1", title = "Helmet Discount", subtitle = "10% off helmets this week", thumbnailRes = null),
    Suggestion(id = "s2", title = "Tune-up Offer", subtitle = "Free check with subscription", thumbnailRes = null)
)