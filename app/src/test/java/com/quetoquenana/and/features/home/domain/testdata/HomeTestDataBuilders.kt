package com.quetoquenana.and.features.home.domain.testdata

import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.announcements.domain.model.AnnouncementMedia
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.AppointmentService
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import java.util.UUID

fun testBike(
    id: UUID = UUID.randomUUID(),
    name: String = "Bike",
    type: String = "ROAD",
    isActive: Boolean = true,
    isPublic: Boolean = true,
    isExternalSync: Boolean = false,
    brand: String? = null,
    model: String? = null,
    year: Int? = null,
    serialNumber: String? = null,
    notes: String? = null,
    odometerKm: Double = 0.0,
    usageTimeMinutes: Int = 0,
    externalGearId: String? = null,
    externalSyncProvider: String = ""
): Bike = Bike(
    id = id,
    name = name,
    type = type,
    status = if (isActive) "ACTIVE" else "RETIRED",
    isPublic = isPublic,
    isExternalSync = isExternalSync,
    brand = brand,
    model = model,
    year = year,
    serialNumber = serialNumber,
    notes = notes,
    odometerKm = odometerKm,
    usageTimeMinutes = usageTimeMinutes,
    externalGearId = externalGearId,
    externalSyncProvider = externalSyncProvider
)

fun testAppointment(
    id: UUID = UUID.randomUUID(),
    dateText: String = "date",
    bikeId: UUID = UUID.randomUUID(),
    bikeName: String = "Bike",
    storeLocationId: UUID? = null,
    storeLocationName: String? = null,
    currency: String? = null,
    scheduledAt: String? = null,
    status: String = "CONFIRMED",
    notes: String? = null,
    deposit: String? = null,
    requestedServices: List<AppointmentService> = emptyList(),
    thumbnailRes: Int? = null
): Appointment = Appointment(
    id = id,
    dateText = dateText,
    bikeId = bikeId,
    bikeName = bikeName,
    storeLocationId = storeLocationId,
    storeLocationName = storeLocationName,
    currency = currency,
    scheduledAt = scheduledAt,
    status = status,
    notes = notes,
    deposit = deposit,
    requestedServices = requestedServices,
    thumbnailRes = thumbnailRes
)

fun testSuggestion(
    id: UUID = UUID.randomUUID(),
    title: String = "Suggestion",
    subtitle: String = "Subtitle"
): Suggestion = Suggestion(
    id = id,
    title = title,
    subtitle = subtitle
)

fun testAnnouncement(
    id: UUID = UUID.randomUUID(),
    title: String = "Announcement",
    subTitle: String? = null,
    description: String = "Description",
    position: Int? = null,
    url: String? = null,
    status: String? = null,
    media: List<AnnouncementMedia> = emptyList(),
    thumbnailRes: Int? = null
): Announcement = Announcement(
    id = id,
    title = title,
    subTitle = subTitle,
    description = description,
    position = position,
    url = url,
    status = status,
    media = media,
    thumbnailRes = thumbnailRes
)
