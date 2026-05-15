package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.AddBikeComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.BikeMediaUploadRequest

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

data class CreateBikeMediaRequestDto(
    val mediaFiles: List<CreateBikeMediaFileRequestDto>
)

data class CreateBikeMediaFileRequestDto(
    val contentType: String,
    val isPrimary: Boolean,
    val name: String,
    val altText: String
)

fun AddBikeComponentRequest.toDto(): AddBikeComponentRequestDto {
    return AddBikeComponentRequestDto(
        name = name,
        type = type,
        brand = brand,
        model = model,
        notes = notes,
        odometerKm = odometerKm,
        usageTimeMinutes = usageTimeMinutes
    )
}

fun List<BikeMediaUploadRequest>.toCreateBikeMediaRequestDto(): CreateBikeMediaRequestDto {
    return CreateBikeMediaRequestDto(
        mediaFiles = map { upload ->
            CreateBikeMediaFileRequestDto(
                contentType = upload.contentType,
                isPrimary = upload.isPrimary,
                name = upload.name,
                altText = upload.altText
            )
        }
    )
}

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
