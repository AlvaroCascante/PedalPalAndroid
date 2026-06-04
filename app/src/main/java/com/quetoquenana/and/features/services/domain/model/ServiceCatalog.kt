package com.quetoquenana.and.features.services.domain.model

import java.util.UUID

data class ServiceProduct(
    val id: UUID,
    val name: String,
    val description: String?,
    val price: String?,
    val status: String?
)

data class ServicePackage(
    val id: UUID,
    val name: String,
    val description: String?,
    val price: String?,
    val status: String?,
    val products: List<ServiceProduct> = emptyList()
)

data class ServiceCatalog(
    val packages: List<ServicePackage> = emptyList(),
    val products: List<ServiceProduct> = emptyList(),
    val lastUpdated: Long? = null,
    val isFromCache: Boolean = false,
    val fetchErrorMessage: String? = null
)
