package com.quetoquenana.and.features.stores.domain.model

import java.util.UUID

data class Store(
    val id: UUID,
    val name: String,
    val locations: List<StoreLocation> = emptyList()
)

data class StoreLocation(
    val id: UUID,
    val storeId: UUID,
    val name: String,
    val storePrefix: String?,
    val website: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val phone: String?,
    val currency: String,
    val timezone: String?,
    val status: String?,
    val serviceCatalogLastUpdatedAt: Long? = null
)
