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
import org.junit.Assert.assertNotNull
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

        val catalog = repository.getCatalog(storeLocationId = "location-1", refresh = true)

        assertEquals(listOf("prod-2", "prod-1"), catalog.products.map(ServiceProduct::id))
        assertNull(catalog.products.firstOrNull { it.id == "prod-package-only" })

        val packageProducts = catalog.packages.single().products
        assertEquals(listOf("prod-package-only", "prod-1"), packageProducts.map(ServiceProduct::id))
        assertEquals("Chain clean", packageProducts.last().name)
        assertEquals("12.50", packageProducts.last().price)
        assertTrue(local.savedProducts.any { it.id == "prod-package-only" && !it.isStandalone })
        assertTrue(local.savedProducts.any { it.id == "prod-1" && it.isStandalone })
        assertTrue(local.savedProducts.all { it.storeLocationId == "location-1" })
        assertNotNull(catalog.lastUpdated)
    }

    @Test
    fun `getCatalog queries cached services by store location`() = kotlinx.coroutines.test.runTest {
        val local = FakeServiceCatalogLocalDataSource()
        val remote = FakeServiceCatalogRemoteDataSource(catalog = ServiceCatalog())
        val repository = ServiceCatalogRepositoryImpl(local = local, remote = remote)

        local.saveCatalog(
            storeLocationId = "location-1",
            packages = emptyList(),
            products = listOf(productEntity(storeLocationId = "location-1", id = "prod-1", name = "Location 1")),
            packageProducts = emptyList(),
            lastUpdated = 100L
        )
        local.saveCatalog(
            storeLocationId = "location-2",
            packages = emptyList(),
            products = listOf(productEntity(storeLocationId = "location-2", id = "prod-2", name = "Location 2")),
            packageProducts = emptyList(),
            lastUpdated = 200L
        )

        val catalog = repository.getCatalog(storeLocationId = "location-2")

        assertEquals(listOf("prod-2"), catalog.products.map(ServiceProduct::id))
        assertEquals(200L, catalog.lastUpdated)
        assertTrue(remote.requestedStoreLocationIds.isEmpty())
    }

    @Test
    fun `getCatalog updates freshness timestamp on successful remote refresh`() = kotlinx.coroutines.test.runTest {
        val local = FakeServiceCatalogLocalDataSource()
        val remote = FakeServiceCatalogRemoteDataSource(
            catalog = ServiceCatalog(
                products = listOf(
                    ServiceProduct(
                        id = "prod-1",
                        name = "Brake check",
                        description = null,
                        price = "10.00",
                        status = "ACTIVE"
                    )
                )
            )
        )
        val repository = ServiceCatalogRepositoryImpl(local = local, remote = remote)

        val beforeRefresh = System.currentTimeMillis()
        val catalog = repository.getCatalog(storeLocationId = "location-1", refresh = true)

        assertTrue(remote.requestedStoreLocationIds == listOf("location-1"))
        assertNotNull(catalog.lastUpdated)
        assertTrue(catalog.lastUpdated!! >= beforeRefresh)
        assertEquals(catalog.lastUpdated, local.lastUpdatedByLocation["location-1"])
    }

    @Test
    fun `getCatalog returns store scoped cache when remote refresh fails`() = kotlinx.coroutines.test.runTest {
        val local = FakeServiceCatalogLocalDataSource()
        local.saveCatalog(
            storeLocationId = "location-1",
            packages = emptyList(),
            products = listOf(productEntity(storeLocationId = "location-1", id = "cached", name = "Cached")),
            packageProducts = emptyList(),
            lastUpdated = 123L
        )
        val remote = FakeServiceCatalogRemoteDataSource(
            catalog = ServiceCatalog(),
            failure = IllegalStateException("offline")
        )
        val repository = ServiceCatalogRepositoryImpl(local = local, remote = remote)

        val catalog = repository.getCatalog(storeLocationId = "location-1", refresh = true)

        assertEquals(listOf("cached"), catalog.products.map(ServiceProduct::id))
        assertEquals(123L, catalog.lastUpdated)
        assertTrue(catalog.isFromCache)
        assertEquals("offline", catalog.fetchErrorMessage)
    }

    private class FakeServiceCatalogRemoteDataSource(
        private val catalog: ServiceCatalog,
        private val failure: Throwable? = null
    ) : ServiceCatalogRemoteDataSource {
        val requestedStoreLocationIds = mutableListOf<String>()

        override suspend fun getCatalog(storeLocationId: String): ServiceCatalog {
            requestedStoreLocationIds += storeLocationId
            failure?.let { throw it }
            return catalog
        }
    }

    private class FakeServiceCatalogLocalDataSource : ServiceCatalogLocalDataSource {
        var savedProducts: List<ServiceProductEntity> = emptyList()
        private var savedPackages: List<ServicePackageEntity> = emptyList()
        private var savedPackageProducts: List<ServicePackageProductEntity> = emptyList()
        val lastUpdatedByLocation = mutableMapOf<String, Long>()

        override suspend fun getProducts(storeLocationId: String): List<ServiceProductEntity> {
            return savedProducts
                .filter { it.storeLocationId == storeLocationId && it.isStandalone }
                .sortedBy(ServiceProductEntity::name)
        }

        override suspend fun getPackages(storeLocationId: String): List<ServicePackageEntity> {
            return savedPackages
                .filter { it.storeLocationId == storeLocationId }
                .sortedBy(ServicePackageEntity::name)
        }

        override suspend fun getProductsForPackage(
            storeLocationId: String,
            packageId: String
        ): List<ServiceProductEntity> {
            val productIds = savedPackageProducts
                .filter { it.storeLocationId == storeLocationId && it.packageId == packageId }
                .map(ServicePackageProductEntity::productId)
                .toSet()
            return savedProducts
                .filter { it.storeLocationId == storeLocationId && it.id in productIds }
                .sortedBy(ServiceProductEntity::name)
        }

        override suspend fun getLastUpdated(storeLocationId: String): Long? {
            return lastUpdatedByLocation[storeLocationId]
        }

        override suspend fun saveCatalog(
            storeLocationId: String,
            packages: List<ServicePackageEntity>,
            products: List<ServiceProductEntity>,
            packageProducts: List<ServicePackageProductEntity>,
            lastUpdated: Long
        ) {
            savedPackages = savedPackages.filterNot { it.storeLocationId == storeLocationId } + packages
            savedProducts = savedProducts.filterNot { it.storeLocationId == storeLocationId } + products
            savedPackageProducts = savedPackageProducts.filterNot { it.storeLocationId == storeLocationId } + packageProducts
            lastUpdatedByLocation[storeLocationId] = lastUpdated
        }
    }

    private fun productEntity(
        storeLocationId: String,
        id: String,
        name: String,
        isStandalone: Boolean = true
    ): ServiceProductEntity {
        return ServiceProductEntity(
            storeLocationId = storeLocationId,
            id = id,
            name = name,
            description = null,
            price = null,
            status = "ACTIVE",
            isStandalone = isStandalone,
            updatedAt = 1L
        )
    }
}
