package com.quetoquenana.and.features.services.data.repository

import com.quetoquenana.and.features.services.data.local.datasource.ServiceCatalogLocalDataSource
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageProductEntity
import com.quetoquenana.and.features.services.data.local.entity.toDomain
import com.quetoquenana.and.features.services.data.local.entity.toEntity
import com.quetoquenana.and.features.services.data.remote.datasource.ServiceCatalogRemoteDataSource
import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import com.quetoquenana.and.features.services.domain.model.ServiceProduct
import com.quetoquenana.and.features.services.domain.repository.ServiceCatalogRepository
import javax.inject.Inject

class ServiceCatalogRepositoryImpl @Inject constructor(
    private val local: ServiceCatalogLocalDataSource,
    private val remote: ServiceCatalogRemoteDataSource
) : ServiceCatalogRepository {

    override suspend fun getCatalog(refresh: Boolean): ServiceCatalog {
        if (refresh || local.getProducts().isEmpty() && local.getPackages().isEmpty()) {
            val catalog = remote.getCatalog()
            val now = System.currentTimeMillis()
            val packageProducts = catalog.packages.flatMap { servicePackage ->
                servicePackage.products.map { product ->
                    ServicePackageProductEntity(
                        packageId = servicePackage.id,
                        productId = product.id
                    )
                }
            }
            val standaloneProductsById = catalog.products.associateBy(ServiceProduct::id)
            val packageProductsById = catalog.packages
                .flatMap { it.products }
                .associateBy(ServiceProduct::id)
            val allProducts = (standaloneProductsById.keys + packageProductsById.keys).map { productId ->
                val standaloneProduct = standaloneProductsById[productId]
                val packageProduct = packageProductsById[productId]
                val mergedProduct = mergeProducts(
                    preferred = standaloneProduct ?: packageProduct!!,
                    fallback = packageProduct ?: standaloneProduct
                )

                mergedProduct.toEntity(
                    currentTimeMillis = now,
                    isStandalone = standaloneProduct != null
                )
            }

            local.saveCatalog(
                packages = catalog.packages.map { it.toEntity(currentTimeMillis = now) },
                products = allProducts,
                packageProducts = packageProducts
            )
        }

        val products = local.getProducts().map { it.toDomain() }
        val packages = local.getPackages().map { packageEntity ->
            packageEntity.toDomain(
                products = local.getProductsForPackage(packageEntity.id).map { it.toDomain() }
            )
        }
        return ServiceCatalog(packages = packages, products = products)
    }

    private fun mergeProducts(
        preferred: ServiceProduct,
        fallback: ServiceProduct?
    ): ServiceProduct {
        if (fallback == null) return preferred

        return preferred.copy(
            name = preferred.name.ifBlank { fallback.name },
            description = preferred.description.orFallback(fallback.description),
            price = preferred.price.orFallback(fallback.price),
            status = preferred.status.orFallback(fallback.status)
        )
    }

    private fun String?.orFallback(fallback: String?): String? {
        return if (this.isNullOrBlank()) fallback else this
    }
}
