package com.quetoquenana.and.features.stores.domain.model

data class Store(
    val id: String,
    val name: String,
    val locations: List<StoreLocation> = emptyList()
)

data class StoreLocation(
    val id: String,
    val storeId: String,
    val name: String,
    val storePrefix: String?,
    val website: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val phone: String?,
    val timezone: String?,
    val status: String?,
    val serviceCatalogLastUpdatedAt: Long? = null
)
