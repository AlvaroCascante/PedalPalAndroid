package com.quetoquenana.and.features.auth.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quetoquenana.and.features.auth.data.local.entity.AuthSessionEntity

@Dao
interface AuthSessionDao {

    @Query("SELECT * FROM auth_session WHERE sessionId = 1 LIMIT 1")
    suspend fun getSession(): AuthSessionEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM auth_session WHERE sessionId = 1 AND isLoggedIn = 1)")
    suspend fun hasActiveSession(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: AuthSessionEntity)

    @Query("DELETE FROM auth_session WHERE sessionId = 1")
    suspend fun clearSession()
}