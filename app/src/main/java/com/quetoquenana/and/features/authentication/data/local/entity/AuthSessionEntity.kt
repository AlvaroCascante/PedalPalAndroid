package com.quetoquenana.and.features.authentication.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.quetoquenana.and.features.authentication.domain.model.AuthSessionResult

@Entity(
    tableName = "auth_session",
    foreignKeys = [
        ForeignKey(
            entity = AuthUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class AuthSessionEntity(
    @PrimaryKey
    val sessionId: Int = 1,
    val userId: String,
    val accessToken: String,
    val refreshToken: String?,
    val expiresAt: Long?,
    val isLoggedIn: Boolean,
    val lastUpdatedAt: Long
)

fun AuthSessionResult.toEntity(userId: String, currentTimeMillis: Long): AuthSessionEntity {
    return AuthSessionEntity(
        sessionId = 1,
        userId = userId,
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresAt = expiresAt,
        isLoggedIn = isLoggedIn,
        lastUpdatedAt = currentTimeMillis
    )
}