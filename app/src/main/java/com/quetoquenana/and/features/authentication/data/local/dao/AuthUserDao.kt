package com.quetoquenana.and.features.authentication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quetoquenana.and.features.authentication.data.local.entity.AuthUserEntity

@Dao
interface AuthUserDao {

    @Query("SELECT * FROM auth_user WHERE id = :userId LIMIT 1")
    suspend fun getById(userId: String): AuthUserEntity?

    @Query("SELECT * FROM auth_user WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): AuthUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: AuthUserEntity)

    @Query("DELETE FROM auth_user")
    suspend fun deleteAll()
}