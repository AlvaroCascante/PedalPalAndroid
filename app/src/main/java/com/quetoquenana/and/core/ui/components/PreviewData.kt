package com.quetoquenana.and.core.ui.components

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.announcements.domain.model.AnnouncementMedia
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeComponentType
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion

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
val previewAppointment = Appointment(
    id = "1",
    dateText = "01/03/2026",
    bikeId = "1",
    bikeName = "Bike A",
    scheduledAt = "2026-03-01T10:00:00Z",
    status = "CONFIRMED",
    thumbnailRes = null
)

val previewAppointments = listOf(
    previewAppointment,
    Appointment(
        id = "2",
        dateText = "01/04/2026",
        bikeId = "2",
        bikeName = "Bike B",
        scheduledAt = "2026-04-01T09:30:00Z",
        status = "REQUESTED",
        thumbnailRes = null
    ),
    Appointment(
        id = "3",
        dateText = "01/05/2026",
        bikeId = "1",
        bikeName = "Bike A",
        scheduledAt = "2026-05-01T08:00:00Z",
        status = "CONFIRMED",
        thumbnailRes = null
    ),
    Appointment(
        id = "4",
        dateText = "01/06/2026",
        bikeId = "2",
        bikeName = "Bike B",
        scheduledAt = "2026-06-01T16:00:00Z",
        status = "IN_PROGRESS",
        thumbnailRes = null
    ),
    Appointment(
        id = "5",
        dateText = "01/07/2026",
        bikeId = "1",
        bikeName = "Bike A",
        scheduledAt = "2026-07-01T11:15:00Z",
        status = "CONFIRMED",
        thumbnailRes = null
    )
    )

val previewSuggestionItem = Suggestion(
    id = "1",
    title = "Check Tire Pressure",
    subtitle = "Ensure your tires are properly inflated for a smooth ride."
)

val previewSuggestions = listOf(
    previewSuggestionItem,
    Suggestion(
        id = "2",
        title = "Lubricate Chain",
        subtitle = "Keep your bike chain lubricated to prevent rust and ensure efficient pedaling."
    ),
    Suggestion(
        id = "3",
        title = "Brake Inspection",
        subtitle = "Regularly inspect your brakes to ensure they."
    )
)

val previewAnnouncementMedia = AnnouncementMedia(
    mediaId = "preview-route-1",
    imageUrl = "https://images.unsplash.com/photo-1485965120184-e220f721d03e"
)

val previewAnnouncementCarouselMedia = listOf(
    previewAnnouncementMedia,
    AnnouncementMedia(
        mediaId = "preview-route-2",
        imageUrl = "https://images.unsplash.com/photo-1507035895480-2b3156c31fc8"
    ),
    AnnouncementMedia(
        mediaId = "preview-route-3",
        imageUrl = "https://images.unsplash.com/photo-1511994298241-608e28f14fde"
    )
)

val previewAnnouncement = Announcement(
    id = "1",
    title = "Explore New Routes",
    subTitle = "Weekend inspiration",
    description = "Discover scenic bike routes in your area.",
    url = "https://example.com/routes",
    media = previewAnnouncementCarouselMedia
)

val previewAnnouncements = listOf(
    previewAnnouncement,
    Announcement(
        id = "2",
        title = "Maintenance Tips",
        description = "Learn how to keep your bike in top condition."
    ),
    Announcement(
        id = "3",
        title = "Upcoming Events",
        description = "Stay informed about local cycling events and meetups."
    )
)

val previewBike = Bike(
    id = "bike-preview",
    name = "Trek Domane",
    type = "ROAD",
    status = "ACTIVE",
    isPublic = true,
    isExternalSync = true,
    brand = "Trek",
    model = "Domane AL 2",
    year = 2024,
    serialNumber = "SN-001",
    notes = "Weekend endurance bike",
    odometerKm = 1240.0,
    usageTimeMinutes = 305,
    externalGearId = "gear-123",
    externalSyncProvider = "Strava"
)

val previewBikes = listOf(
    previewBike,
    previewBike.copy(
        id = "bike-preview-2",
        name = "Specialized Sirrus",
        type = "HYBRID",
        isExternalSync = false,
        brand = "Specialized",
        model = "Sirrus X 2.0",
        year = 2023,
        serialNumber = "SN-002",
        notes = "Commute and errands",
        odometerKm = 640.0,
        usageTimeMinutes = 92,
        externalGearId = null,
        externalSyncProvider = ""
    )
)


val previewComponentTypes = listOf(
    BikeComponentType(
        id = "type-1",
        category = "DRIVETRAIN",
        code = "CHAIN",
        codeDescription = "Chain",
        status = "ACTIVE",
        position = 1
    ),
    BikeComponentType(
        id = "type-2",
        category = "WHEELS",
        code = "TIRES",
        codeDescription = "Tires",
        status = "ACTIVE",
        position = 2
    )
)