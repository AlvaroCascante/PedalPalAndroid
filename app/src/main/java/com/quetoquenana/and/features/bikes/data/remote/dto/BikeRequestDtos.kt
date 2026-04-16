package com.quetoquenana.and.features.bikes.data.remote.dto

data class UpdateBikeRequestDto(
    val name: String?,
    val brand: String?,
    val model: String?,
    val year: Int?,
    val type: String?,
    val serialNumber: String?,
    val notes: String?,
    val odometerKm: Int?,
    val usageTimeMinutes: Int?,
    val isPublic: Boolean?,
    val isExternalSync: Boolean?,
    val externalGearId: String?,
    val externalSyncProvider: String?
)

data class UpdateBikeStatusRequestDto(
    val status: String
)

data class AddBikeComponentRequestDto(
    val name: String,
    val type: String,
    val brand: String?,
    val model: String?,
    val notes: String?,
    val odometerKm: Int,
    val usageTimeMinutes: Int
)

data class UpdateBikeComponentRequestDto(
    val name: String?,
    val type: String?,
    val brand: String?,
    val model: String?,
    val notes: String?,
    val odometerKm: Int?,
    val usageTimeMinutes: Int?
)

data class UpdateBikeComponentStatusRequestDto(
    val status: String
)
