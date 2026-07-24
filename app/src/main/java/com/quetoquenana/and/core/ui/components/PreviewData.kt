package com.quetoquenana.and.core.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.quetoquenana.and.R
import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.AppointmentService
import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.announcements.domain.model.AnnouncementMedia
import com.quetoquenana.and.features.authentication.ui.CompleteProfileUiState
import com.quetoquenana.and.features.authentication.ui.LoginUiState
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.ui.AddBikeUiState
import com.quetoquenana.and.features.bikes.domain.model.ComponentType
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.ui.BikesUiState
import com.quetoquenana.and.features.bikes.ui.StravaImportUiState
import com.quetoquenana.and.features.home.ui.HeaderSection
import com.quetoquenana.and.features.home.ui.HomeUiState
import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import com.quetoquenana.and.features.services.domain.model.ServicePackage
import com.quetoquenana.and.features.services.domain.model.ServiceProduct
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import java.util.UUID

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


@Composable
fun BasePreviewContainer(
    content: @Composable () -> Unit
) {
    PedalPalTheme {
        Column(modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

/** Preview data for the app */
val previewAppointment = Appointment(
    id = UUID.randomUUID(),
    dateText = "01/03/2026",
    bikeId = UUID.randomUUID(),
    bikeName = "Bike A",
    scheduledAt = "2026-03-01T10:00:00Z",
    status = "CONFIRMED",
    thumbnailRes = null
)

val previewAppointments = listOf(
    previewAppointment,
    Appointment(
        id = UUID.randomUUID(),
        dateText = "01/04/2026",
        bikeId = UUID.randomUUID(),
        bikeName = "Bike B",
        scheduledAt = "2026-04-01T09:30:00Z",
        status = "REQUESTED",
        thumbnailRes = null
    ),
    Appointment(
        id = UUID.randomUUID(),
        dateText = "01/05/2026",
        bikeId = UUID.randomUUID(),
        bikeName = "Bike A",
        scheduledAt = "2026-05-01T08:00:00Z",
        status = "CONFIRMED",
        thumbnailRes = null
    ),
    Appointment(
        id = UUID.randomUUID(),
        dateText = "01/06/2026",
        bikeId = UUID.randomUUID(),
        bikeName = "Bike B",
        scheduledAt = "2026-06-01T16:00:00Z",
        status = "IN_PROGRESS",
        thumbnailRes = null
    ),
    Appointment(
        id = UUID.randomUUID(),
        dateText = "01/07/2026",
        bikeId = UUID.randomUUID(),
        bikeName = "Bike A",
        scheduledAt = "2026-07-01T11:15:00Z",
        status = "CONFIRMED",
        thumbnailRes = null
    )
    )

val previewAppointmentDetail = Appointment(
    id = UUID.randomUUID(),
    dateText = "May 22, 2026 · 9:30 AM",
    bikeId = UUID.randomUUID(),
    bikeName = "Trek Domane AL 2",
    storeLocationId = UUID.randomUUID(),
    storeLocationName = "San José Workshop",
    currency = "CRC",
    scheduledAt = "2026-05-22T09:30:00Z",
    status = "CONFIRMED",
    notes = "Please check drivetrain noise under load and inspect rear brake rub.",
    deposit = "0",
    requestedServices = listOf(
        AppointmentService(
            id = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            productName = "Full tune-up",
            price = "25000"
        ),
        AppointmentService(
            id = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            productName = "Chain replacement",
            price = "18000"
        )
    )
)

val previewAppointmentAttachments = listOf(
    MediaAsset(
        referenceId = UUID.randomUUID(),
        referenceType = MediaReferenceType.APPOINTMENT_DEPOSIT,
        mediaId = UUID.randomUUID(),
        url = "https://example.com/deposit.png",
        contentType = "IMAGE_PNG",
        name = MediaReferenceType.APPOINTMENT_DEPOSIT.mediaName,
        altText = "SINPE deposit receipt",
        isPrivate = true,
        urlExpireAt = null,
        updatedAt = 0L,
        fetchedAt = 0L,
    )
)

val previewSuggestionItem = Suggestion(
    id = UUID.randomUUID(),
    title = "Check Tire Pressure",
    subtitle = "Ensure your tires are properly inflated for a smooth ride."
)

val previewSuggestions = listOf(
    previewSuggestionItem,
    Suggestion(
        id = UUID.randomUUID(),
        title = "Lubricate Chain",
        subtitle = "Keep your bike chain lubricated to prevent rust and ensure efficient pedaling."
    ),
    Suggestion(
        id = UUID.randomUUID(),
        title = "Brake Inspection",
        subtitle = "Regularly inspect your brakes to ensure they."
    )
)

val previewAnnouncementMedia = AnnouncementMedia(
    mediaId = UUID.randomUUID(),
    imageUrl = "https://images.unsplash.com/photo-1485965120184-e220f721d03e"
)

val previewAnnouncementCarouselMedia = listOf(
    previewAnnouncementMedia,
    AnnouncementMedia(
        mediaId = UUID.randomUUID(),
        imageUrl = "https://images.unsplash.com/photo-1507035895480-2b3156c31fc8",
        expiresAt = "2026-12-31T23:59:59Z"
    ),
    AnnouncementMedia(
        mediaId = UUID.randomUUID(),
        imageUrl = "https://images.unsplash.com/photo-1511994298241-608e28f14fde",
        expiresAt = "2026-12-31T23:59:59Z"
    )
)

val previewAnnouncement = Announcement(
    id = UUID.randomUUID(),
    title = "Explore New Routes",
    subTitle = "Weekend inspiration",
    description = "Discover scenic bike routes in your area.",
    url = "https://example.com/routes",
    media = previewAnnouncementCarouselMedia
)

val previewAnnouncements = listOf(
    previewAnnouncement,
    Announcement(
        id = UUID.randomUUID(),
        title = "Maintenance Tips",
        description = "Learn how to keep your bike in top condition."
    ),
    Announcement(
        id = UUID.randomUUID(),
        title = "Upcoming Events",
        description = "Stay informed about local cycling events and meetups."
    )
)

val previewBike = Bike(
    id = UUID.randomUUID(),
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
        id = UUID.randomUUID(),
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

/** Preview AddBikeUiState for use in composable previews */
val previewAddBikeUiState = AddBikeUiState(
    name = "Trek Domane",
    type = BikeType.ROAD,
    brand = "Trek",
    model = "AL 2",
    year = "2024",
    serialNumber = "SN-001",
    notes = "Weekend bike",
    isPublic = true
)

val previewAddBikeUiStateError = AddBikeUiState(
    name = "",
    nameErrorRes = R.string.error_bike_name_is_required,
    type = null,
    typeErrorRes = R.string.error_bike_type_is_required,
    brand = "Trek",
    model = "AL 2",
    year = "2024",
    serialNumber = "SN-001",
    notes = "Weekend bike",
    isPublic = true
)

val previewComponentTypes = listOf(
    ComponentType(
        id = UUID.randomUUID(),
        category = "DRIVETRAIN",
        code = "CHAIN",
        codeDescription = "Chain",
        status = "ACTIVE",
        position = 1
    ),
    ComponentType(
        id = UUID.randomUUID(),
        category = "WHEELS",
        code = "TIRES",
        codeDescription = "Tires",
        status = "ACTIVE",
        position = 2
    )
)

val previewStore = Store(
    id = UUID.randomUUID(),
    name = "PedalPal Central",
    locations = listOf(
        StoreLocation(
            id = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            name = "Workshop",
            storePrefix = "CENTRAL",
            website = "https://pedalpal.example",
            address = "123 Bike Lane",
            latitude = 40.4168,
            longitude = -3.7038,
            phone = "+34 600 000 000",
            currency = "CRC",
            timezone = "Europe/Madrid",
            status = "ACTIVE",
            serviceCatalogLastUpdatedAt = 1_715_788_800_000
        )
    )
)

val previewServiceCatalog = ServiceCatalog(
    packages = listOf(
        ServicePackage(
            id = UUID.randomUUID(),
            name = "Full tune-up",
            description = "Brake, drivetrain, and shifting inspection.",
            price = "79.99",
            status = "ACTIVE"
        )
    ),
    products = listOf(
        ServiceProduct(
            id = UUID.randomUUID(),
            name = "Chain replacement",
            description = "Install and size a new chain.",
            price = "24.99",
            status = "ACTIVE"
        )
    )
)

val stravaImportUiState = StravaImportUiState(
    bikes = listOf(
        StravaBike(
            id = "1",
            name = "Strava Road Bike",
            nickname = "Fast one",
            primary = true,
            retired = false,
            distance = 1200.0,
            brandName = "StravaBrand",
            modelName = "StravaModel",
            frameType = "Road",
            description = "A bike imported from Strava"
        )
    )
)

class LoginUiStateProvider: PreviewParameterProvider<LoginUiState> {
    override val values: Sequence<LoginUiState> = sequenceOf(element = LoginUiState())

    override fun getDisplayName(index: Int): String { return "Login" }
}

class BikesHomeUiStateProvider: PreviewParameterProvider<HomeUiState> {
    override val values: Sequence<HomeUiState> = sequenceOf(
        HomeUiState(announcements = previewAnnouncements,
            headerSection = HeaderSection.Content(
                appointments = previewAppointments
            ),
            suggestions = previewSuggestions,
            bikes = previewBikes,
        )
    )

    override fun getDisplayName(index: Int): String {
        return "HomeUiState Bikes"
    }
}

class NoBikesHomeUiStateProvider: PreviewParameterProvider<HomeUiState> {
    override val values: Sequence<HomeUiState> = sequenceOf(
        HomeUiState(announcements = previewAnnouncements,
            headerSection = HeaderSection.NoBikes(),
            suggestions = previewSuggestions,
            bikes = emptyList(),
        )
    )

    override fun getDisplayName(index: Int): String {
        return "HomeUiState No Bikes"
    }
}

class NoAppointmentsHomeUiStateProvider: PreviewParameterProvider<HomeUiState> {
    override val values: Sequence<HomeUiState> = sequenceOf(
        HomeUiState(announcements = previewAnnouncements,
            headerSection = HeaderSection.Content(
                appointments = emptyList()
            ),
            suggestions = previewSuggestions,
            bikes = previewBikes,
        )
    )

    override fun getDisplayName(index: Int): String {
        return "HomeUiState No Appointments"
    }
}

class LoadingHomeUiStateProvider: PreviewParameterProvider<HomeUiState> {
    override val values: Sequence<HomeUiState> = sequenceOf(
        HomeUiState(announcements = previewAnnouncements,
            headerSection = HeaderSection.Loading,
            suggestions = previewSuggestions,
            bikes = previewBikes,
        )
    )

    override fun getDisplayName(index: Int): String {
        return "HomeUiState Loading"
    }
}

class BikesUiStateProvider: PreviewParameterProvider<BikesUiState> {
    override val values: Sequence<BikesUiState> = sequenceOf(
        BikesUiState(
            bikes = previewBikes,
            isRefreshing = false,
            isLoading = false
        )
    )

    override fun getDisplayName(index: Int): String {
        return "Bikes UiState"
    }
}

class NoBikesUiStateProvider: PreviewParameterProvider<BikesUiState> {
    override val values: Sequence<BikesUiState> = sequenceOf(
        BikesUiState(
            bikes = emptyList(),
            isRefreshing = false,
            isLoading = false
        )
    )

    override fun getDisplayName(index: Int): String {
        return "NoBikes UiState"
    }
}
class LoadingBikesUiStateProvider: PreviewParameterProvider<BikesUiState> {
    override val values: Sequence<BikesUiState> = sequenceOf(
        BikesUiState(isLoading = true)
    )

    override fun getDisplayName(index: Int): String {
        return "Loading"
    }
}

class AddBikeUiStateProvider: PreviewParameterProvider<AddBikeUiState> {
    override val values: Sequence<AddBikeUiState> = sequenceOf(
        element = previewAddBikeUiState
    )

    override fun getDisplayName(index: Int): String {
        return "Add Bike UiState"
    }
}


class CompleteProfileUiStateProvider: PreviewParameterProvider<CompleteProfileUiState> {
    override val values: Sequence<CompleteProfileUiState> = sequenceOf(
        CompleteProfileUiState(
            nickname = "Ksknt",
            idNumber = "1-1108-0683",
            lastName = "Cascante",
            firstName = "Alvaro",
            isSaving = false
        )
    )

    override fun getDisplayName(index: Int): String {
        return "CompleteProfileUiState"
    }
}