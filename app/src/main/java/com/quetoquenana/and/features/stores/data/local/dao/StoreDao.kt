package com.quetoquenana.and.features.stores.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.quetoquenana.and.features.stores.data.local.entity.StoreEntity
import com.quetoquenana.and.features.stores.data.local.entity.StoreLocationEntity

@Dao
interface StoreDao {

    @Query("SELECT * FROM stores ORDER BY name ASC")
    suspend fun getStores(): List<StoreEntity>

    @Query("SELECT * FROM store_locations WHERE storeId = :storeId ORDER BY name ASC")
    suspend fun getLocationsForStore(storeId: String): List<StoreLocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStores(stores: List<StoreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLocations(locations: List<StoreLocationEntity>)

    @Query("DELETE FROM store_locations")
    suspend fun clearLocations()

    @Query("DELETE FROM stores")
    suspend fun clearStores()

    @Transaction
    suspend fun replaceAll(
        stores: List<StoreEntity>,
        locations: List<StoreLocationEntity>
    ) {
        clearLocations()
        clearStores()
        upsertStores(stores)
        upsertLocations(locations)
    }
}
