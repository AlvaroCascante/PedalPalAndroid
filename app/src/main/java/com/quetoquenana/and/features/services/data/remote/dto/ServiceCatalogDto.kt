package com.quetoquenana.and.features.services.data.remote.dto

import com.quetoquenana.and.features.services.domain.model.ServicePackage
import com.quetoquenana.and.features.services.domain.model.ServiceProduct
import java.math.BigDecimal

data class ProductResponseDto(
    val id: String,
    val name: String,
    val description: String?,
    val price: BigDecimal?,
    val status: String?
)

data class ProductPackageResponseDto(
    val id: String,
    val name: String,
    val description: String?,
    val price: BigDecimal?,
    val status: String?,
    val products: List<ProductResponseDto> = emptyList()
)

fun ProductResponseDto.toDomain(): ServiceProduct {
    return ServiceProduct(
        id = id,
        name = name,
        description = description,
        price = price?.toPlainString(),
        status = status
    )
}

fun ProductPackageResponseDto.toDomain(): ServicePackage {
    return ServicePackage(
        id = id,
        name = name,
        description = description,
        price = price?.toPlainString(),
        status = status,
        products = products.map { it.toDomain() }
    )
}
