package com.quetoquenana.and.features.profile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quetoquenana.and.features.profile.data.local.entity.ProfileEntity
import java.util.UUID

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles WHERE id = :userId LIMIT 1")
    suspend fun getCurrentProfile(userId: UUID): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: ProfileEntity)

    @Query("DELETE FROM profiles")
    suspend fun deleteAll()
}

