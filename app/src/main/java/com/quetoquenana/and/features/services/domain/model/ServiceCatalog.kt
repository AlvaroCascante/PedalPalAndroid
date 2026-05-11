package com.quetoquenana.and.features.services.domain.model

data class ServiceProduct(
    val id: String,
    val name: String,
    val description: String?,
    val price: String?,
    val status: String?
)

data class ServicePackage(
    val id: String,
    val name: String,
    val description: String?,
    val price: String?,
    val status: String?,
    val products: List<ServiceProduct> = emptyList()
)

data class ServiceCatalog(
    val packages: List<ServicePackage> = emptyList(),
    val products: List<ServiceProduct> = emptyList()
)
