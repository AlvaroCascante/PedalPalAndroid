package com.quetoquenana.and.features.services.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageEntity
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageProductEntity
import com.quetoquenana.and.features.services.data.local.entity.ServiceProductEntity

@Dao
interface ServiceCatalogDao {

    @Query(
        """
        SELECT * FROM service_products
        WHERE storeLocationId = :storeLocationId AND isStandalone = 1
        ORDER BY name ASC
        """
    )
    suspend fun getProducts(storeLocationId: String): List<ServiceProductEntity>

    @Query("SELECT * FROM service_packages WHERE storeLocationId = :storeLocationId ORDER BY name ASC")
    suspend fun getPackages(storeLocationId: String): List<ServicePackageEntity>

    @Query("SELECT serviceCatalogLastUpdatedAt FROM store_locations WHERE id = :storeLocationId")
    suspend fun getLastUpdated(storeLocationId: String): Long?

    @Query(
        """
        SELECT service_products.* FROM service_products
        INNER JOIN service_package_products ON service_products.id = service_package_products.productId
            AND service_products.storeLocationId = service_package_products.storeLocationId
        WHERE service_package_products.storeLocationId = :storeLocationId
            AND service_package_products.packageId = :packageId
        ORDER BY service_products.name ASC
        """
    )
    suspend fun getProductsForPackage(storeLocationId: String, packageId: String): List<ServiceProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProducts(products: List<ServiceProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPackages(packages: List<ServicePackageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPackageProducts(packageProducts: List<ServicePackageProductEntity>)

    @Query("DELETE FROM service_package_products WHERE storeLocationId = :storeLocationId")
    suspend fun clearPackageProducts(storeLocationId: String)

    @Query("DELETE FROM service_packages WHERE storeLocationId = :storeLocationId")
    suspend fun clearPackages(storeLocationId: String)

    @Query("DELETE FROM service_products WHERE storeLocationId = :storeLocationId")
    suspend fun clearProducts(storeLocationId: String)

    @Query(
        """
        UPDATE store_locations
        SET serviceCatalogLastUpdatedAt = :lastUpdated
        WHERE id = :storeLocationId
        """
    )
    suspend fun updateLastUpdated(storeLocationId: String, lastUpdated: Long)

    @Transaction
    suspend fun replaceAll(
        storeLocationId: String,
        packages: List<ServicePackageEntity>,
        products: List<ServiceProductEntity>,
        packageProducts: List<ServicePackageProductEntity>,
        lastUpdated: Long
    ) {
        clearPackageProducts(storeLocationId)
        clearPackages(storeLocationId)
        clearProducts(storeLocationId)
        upsertProducts(products)
        upsertPackages(packages)
        upsertPackageProducts(packageProducts)
        updateLastUpdated(storeLocationId = storeLocationId, lastUpdated = lastUpdated)
    }
}
