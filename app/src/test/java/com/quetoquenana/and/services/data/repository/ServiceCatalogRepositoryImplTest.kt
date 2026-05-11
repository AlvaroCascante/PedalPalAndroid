package com.quetoquenana.and.services.data.repository

import com.quetoquenana.and.features.services.data.local.datasource.ServiceCatalogLocalDataSource
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageEntity
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageProductEntity
import com.quetoquenana.and.features.services.data.local.entity.ServiceProductEntity
import com.quetoquenana.and.features.services.data.repository.ServiceCatalogRepositoryImpl
import com.quetoquenana.and.features.services.data.remote.datasource.ServiceCatalogRemoteDataSource
import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import com.quetoquenana.and.features.services.domain.model.ServicePackage
import com.quetoquenana.and.features.services.domain.model.ServiceProduct
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceCatalogRepositoryImplTest {

    @Test
    fun `getCatalog returns only standalone products while keeping package products linked`() = kotlinx.coroutines.test.runTest {
        val local = FakeServiceCatalogLocalDataSource()
        val remote = FakeServiceCatalogRemoteDataSource(
            catalog = ServiceCatalog(
                packages = listOf(
                    ServicePackage(
                        id = "pkg-1",
                        name = "Tune Up",
                        description = "Full maintenance",
                        price = "59.90",
                        status = "ACTIVE",
                        products = listOf(
                            ServiceProduct(
                                id = "prod-1",
                                name = "",
                                description = null,
                                price = null,
                                status = null
                            ),
                            ServiceProduct(
                                id = "prod-package-only",
                                name = "",
                                description = null,
                                price = null,
                                status = null
                            )
                        )
                    )
                ),
                products = listOf(
                    ServiceProduct(
                        id = "prod-1",
                        name = "Chain clean",
                        description = "Drivetrain service",
                        price = "12.50",
                        status = "ACTIVE"
                    ),
                    ServiceProduct(
                        id = "prod-2",
                        name = "Brake check",
                        description = "Safety check",
                        price = "9.99",
                        status = "ACTIVE"
                    )
                )
            )
        )

        val repository = ServiceCatalogRepositoryImpl(local = local, remote = remote)

        val catalog = repository.getCatalog(refresh = true)

        assertEquals(listOf("prod-2", "prod-1"), catalog.products.map(ServiceProduct::id))
        assertNull(catalog.products.firstOrNull { it.id == "prod-package-only" })

        val packageProducts = catalog.packages.single().products
        assertEquals(listOf("prod-package-only", "prod-1"), packageProducts.map(ServiceProduct::id))
        assertEquals("Chain clean", packageProducts.last().name)
        assertEquals("12.50", packageProducts.last().price)
        assertTrue(local.savedProducts.any { it.id == "prod-package-only" && !it.isStandalone })
        assertTrue(local.savedProducts.any { it.id == "prod-1" && it.isStandalone })
    }

    private class FakeServiceCatalogRemoteDataSource(
        private val catalog: ServiceCatalog
    ) : ServiceCatalogRemoteDataSource {
        override suspend fun getCatalog(): ServiceCatalog = catalog
    }

    private class FakeServiceCatalogLocalDataSource : ServiceCatalogLocalDataSource {
        var savedProducts: List<ServiceProductEntity> = emptyList()
        private var savedPackages: List<ServicePackageEntity> = emptyList()
        private var savedPackageProducts: List<ServicePackageProductEntity> = emptyList()

        override suspend fun getProducts(): List<ServiceProductEntity> {
            return savedProducts
                .filter(ServiceProductEntity::isStandalone)
                .sortedBy(ServiceProductEntity::name)
        }

        override suspend fun getPackages(): List<ServicePackageEntity> {
            return savedPackages.sortedBy(ServicePackageEntity::name)
        }

        override suspend fun getProductsForPackage(packageId: String): List<ServiceProductEntity> {
            val productIds = savedPackageProducts
                .filter { it.packageId == packageId }
                .map(ServicePackageProductEntity::productId)
                .toSet()
            return savedProducts
                .filter { it.id in productIds }
                .sortedBy(ServiceProductEntity::name)
        }

        override suspend fun saveCatalog(
            packages: List<ServicePackageEntity>,
            products: List<ServiceProductEntity>,
            packageProducts: List<ServicePackageProductEntity>
        ) {
            savedPackages = packages
            savedProducts = products
            savedPackageProducts = packageProducts
        }
    }
}

