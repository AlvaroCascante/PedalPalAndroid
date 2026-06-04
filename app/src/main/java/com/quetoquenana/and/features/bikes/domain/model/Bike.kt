package com.quetoquenana.and.features.bikes.domain.model

import java.util.UUID

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
    val id: UUID,
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
    val components: List<Component> = emptyList()
)

fun Bike.isActive(): Boolean = status.equals(other = "ACTIVE", ignoreCase = true)

data class Component(
    val id: UUID,
    val type: String,
    val name: String,
    val status: String,
    val brand: String?,
    val model: String?,
    val notes: String?,
    val odometerKm: Int,
    val usageTimeMinutes: Int
)

data class AddComponentRequest(
    val name: String,
    val type: String,
    val brand: String?,
    val model: String?,
    val notes: String?,
    val odometerKm: Int,
    val usageTimeMinutes: Int
)

data class ComponentType(
    val id: UUID,
    val category: String,
    val code: String,
    val codeDescription: String,
    val status: String,
    val position: Int?
)

data class BikeHistory(
    val id: UUID,
    val bikeId: UUID,
    val occurredAt: String,
    val performedBy: String?,
    val type: BikeHistoryType,
    val rawType: String,
    val payload: String
)

enum class BikeHistoryType(
    val translationKey: String
) {
    CREATED("bike.history.created"),
    UPDATED("bike.history.updated"),
    STATUS_CHANGED("bike.history.status.changed"),
    COMPONENT_ADDED("bike.history.component.added"),
    COMPONENT_UPDATED("bike.history.component.updated"),
    COMPONENT_REPLACED("bike.history.component.replaced"),
    COMPONENT_STATUS_CHANGED("bike.history.component.status.changed"),
    UNKNOWN("bike.history.unknown");

    companion object {
        fun from(value: String): BikeHistoryType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }
}

