package com.quetoquenana.and.features.stores.data.remote.dto

import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import java.util.UUID

data class StoreResponseDto(
    val id: UUID,
    val name: String,
    val locations: List<StoreLocationResponseDto> = emptyList()
)

data class StoreLocationResponseDto(
    val id: UUID,
    val name: String,
    val storePrefix: String?,
    val website: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val phone: String?,
    val currency: String,
    val timezone: String?,
    val status: String?
)

fun StoreResponseDto.toDomain(): Store {
    return Store(
        id = id,
        name = name,
        locations = locations.map { it.toDomain(storeId = id) }
    )
}

private fun StoreLocationResponseDto.toDomain(storeId: UUID): StoreLocation {
    return StoreLocation(
        id = id,
        storeId = storeId,
        name = name,
        storePrefix = storePrefix,
        website = website,
        address = address,
        latitude = latitude,
        longitude = longitude,
        phone = phone,
        currency = currency,
        timezone = timezone,
        status = status
    )
}
