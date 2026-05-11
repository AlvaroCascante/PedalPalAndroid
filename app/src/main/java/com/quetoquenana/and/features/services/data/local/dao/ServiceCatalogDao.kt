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

    @Query("SELECT * FROM service_products WHERE isStandalone = 1 ORDER BY name ASC")
    suspend fun getProducts(): List<ServiceProductEntity>

    @Query("SELECT * FROM service_packages ORDER BY name ASC")
    suspend fun getPackages(): List<ServicePackageEntity>

    @Query(
        """
        SELECT service_products.* FROM service_products
        INNER JOIN service_package_products ON service_products.id = service_package_products.productId
        WHERE service_package_products.packageId = :packageId
        ORDER BY service_products.name ASC
        """
    )
    suspend fun getProductsForPackage(packageId: String): List<ServiceProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProducts(products: List<ServiceProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPackages(packages: List<ServicePackageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPackageProducts(packageProducts: List<ServicePackageProductEntity>)

    @Query("DELETE FROM service_package_products")
    suspend fun clearPackageProducts()

    @Query("DELETE FROM service_packages")
    suspend fun clearPackages()

    @Query("DELETE FROM service_products")
    suspend fun clearProducts()

    @Transaction
    suspend fun replaceAll(
        packages: List<ServicePackageEntity>,
        products: List<ServiceProductEntity>,
        packageProducts: List<ServicePackageProductEntity>
    ) {
        clearPackageProducts()
        clearPackages()
        clearProducts()
        upsertProducts(products)
        upsertPackages(packages)
        upsertPackageProducts(packageProducts)
    }
}
