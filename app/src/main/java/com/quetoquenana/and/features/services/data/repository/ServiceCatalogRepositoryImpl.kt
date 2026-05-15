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

    override suspend fun getCatalog(storeLocationId: String, refresh: Boolean): ServiceCatalog {
        val cachedCatalog = getCachedCatalog(storeLocationId = storeLocationId)
        if (refresh || cachedCatalog.isEmpty()) {
            try {
                refreshCatalog(storeLocationId = storeLocationId)
            } catch (throwable: Throwable) {
                if (cachedCatalog.hasContent()) {
                    return cachedCatalog.copy(
                        isFromCache = true,
                        fetchErrorMessage = throwable.message ?: "Unable to refresh services"
                    )
                }
                throw throwable
            }
        }

        return getCachedCatalog(storeLocationId = storeLocationId)
    }

    private suspend fun refreshCatalog(storeLocationId: String) {
        val catalog = remote.getCatalog(storeLocationId = storeLocationId)
        val now = System.currentTimeMillis()
        val packageProducts = catalog.packages.flatMap { servicePackage ->
            servicePackage.products.map { product ->
                ServicePackageProductEntity(
                    storeLocationId = storeLocationId,
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
                storeLocationId = storeLocationId,
                currentTimeMillis = now,
                isStandalone = standaloneProduct != null
            )
        }

        local.saveCatalog(
            storeLocationId = storeLocationId,
            packages = catalog.packages.map {
                it.toEntity(storeLocationId = storeLocationId, currentTimeMillis = now)
            },
            products = allProducts,
            packageProducts = packageProducts,
            lastUpdated = now
        )
    }

    private suspend fun getCachedCatalog(storeLocationId: String): ServiceCatalog {
        val products = local.getProducts(storeLocationId = storeLocationId).map { it.toDomain() }
        val packages = local.getPackages(storeLocationId = storeLocationId).map { packageEntity ->
            packageEntity.toDomain(
                products = local.getProductsForPackage(
                    storeLocationId = storeLocationId,
                    packageId = packageEntity.id
                ).map { it.toDomain() }
            )
        }
        return ServiceCatalog(
            packages = packages,
            products = products,
            lastUpdated = local.getLastUpdated(storeLocationId = storeLocationId)
        )
    }

    private fun ServiceCatalog.isEmpty(): Boolean = packages.isEmpty() && products.isEmpty()

    private fun ServiceCatalog.hasContent(): Boolean = !isEmpty()

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
