package com.quetoquenana.and.pedalpal.core.ui.components

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.quetoquenana.and.pedalpal.features.appointments.domain.model.Appointment
import com.quetoquenana.and.pedalpal.features.landing.domain.model.LandingPageItem
import com.quetoquenana.and.pedalpal.features.suggestions.domain.model.Suggestion

@Preview(
    name = "Small Font",
    group = "Font scales",
    fontScale = 0.5f,
    apiLevel = 35
)
@Preview(
    name = "Large Font",
    group = "Font scales",
    fontScale = 1.5f,
    apiLevel = 35
)
@Preview(
    name = "Large Font",
    group = "Font scales",
    fontScale = 2f,
    apiLevel = 35
)
annotation class FonsScalePreviews

@Preview(
    name = "Dark Theme",
    group = "UI mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true,
    apiLevel = 35
)
@Preview(
    name = "Light Theme",
    group = "UI mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true,
    apiLevel = 35
)
annotation class DarkLightPreviews

/** Preview data for the app */
val previewAppointments = listOf(
    Appointment(id = "1", dateText = "01/03/2026", bikeId = "1", bikeName = "Bike A", thumbnailRes = null),
    Appointment(id = "2", dateText = "01/04/2026", bikeId = "2", bikeName = "Bike B", thumbnailRes = null),
    Appointment(id = "3", dateText = "01/05/2026", bikeId = "1", bikeName = "Bike A", thumbnailRes = null),
    Appointment(id = "4", dateText = "01/06/2026", bikeId = "2", bikeName = "Bike B", thumbnailRes = null),
    Appointment(id = "5", dateText = "01/07/2026", bikeId = "1", bikeName = "Bike A", thumbnailRes = null)
    )

val previewSuggestion = listOf(
    Suggestion(id = "1", title = "Check Tire Pressure", subtitle = "Ensure your tires are properly inflated for a smooth ride."),
    Suggestion(id = "2", title = "Lubricate Chain", subtitle = "Keep your bike chain lubricated to prevent rust and ensure efficient pedaling."),
    Suggestion(id = "3", title = "Brake Inspection", subtitle = "Regularly inspect your brakes to ensure they.")
)

val previewLandingPageItem = listOf(
    LandingPageItem(id = "1", title = "Explore New Routes", description = "Discover scenic bike routes in your area."),
    LandingPageItem(id = "2", title = "Maintenance Tips", description = "Learn how to keep your bike in top condition."),
    LandingPageItem(id = "3", title = "Upcoming Events", description = "Stay informed about local cycling events and meetups.")

)