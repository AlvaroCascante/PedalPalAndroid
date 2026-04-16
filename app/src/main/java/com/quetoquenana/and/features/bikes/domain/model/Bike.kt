package com.quetoquenana.and.features.bikes.domain.model

enum class BikeType {
    ROAD,
    GRAVEL,
    MTB,
    E_BIKE,
    BMX,
    HYBRID,
    OTHER
}

data class Bike(
    val id: String,
    val name: String,
    val type: String,
    val status: String,
    val isPublic: Boolean,
    val isExternalSync: Boolean,
    val brand: String?,
    val model: String?,
    val year: Int?,
    val serialNumber: String?,
    val notes: String?,
    val odometerKm: Double,
    val usageTimeMinutes: Int,
    val externalGearId: String?,
    val externalSyncProvider: String,
    val components: List<BikeComponent> = emptyList()
)

data class BikeComponent(
    val id: String,
    val type: String,
    val name: String,
    val status: String,
    val brand: String?,
    val model: String?,
    val notes: String?,
    val odometerKm: Int,
    val usageTimeMinutes: Int
)

data class BikeHistory(
    val id: String,
    val bikeId: String,
    val occurredAt: String,
    val performedBy: String?,
    val type: String,
    val payload: String
)
