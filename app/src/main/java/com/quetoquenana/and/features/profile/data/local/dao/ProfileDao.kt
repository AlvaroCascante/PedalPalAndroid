package com.quetoquenana.and.features.profile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quetoquenana.and.features.profile.data.local.entity.ProfileEntity

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles WHERE id = :userId LIMIT 1")
    suspend fun getById(userId: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: ProfileEntity)

    @Query("DELETE FROM profiles")
    suspend fun deleteAll()
}

