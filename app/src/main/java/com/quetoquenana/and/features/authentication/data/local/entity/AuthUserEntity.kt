package com.quetoquenana.and.features.authentication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quetoquenana.and.features.authentication.domain.model.AuthUserResult
import java.util.UUID

@Entity(tableName = "auth_user")
data class AuthUserEntity(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val lastname: String?,
    val idNumber: String?,
    val nickname: String?,
    val email: String?,
    val profileCompleted: Boolean,
    val updatedAt: Long
)

fun AuthUserResult.toEntity(id: UUID, currentTimeMillis: Long): AuthUserEntity {
    return AuthUserEntity(
        id = id,
        name = name,
        lastname = lastname,
        idNumber = idNumber,
        nickname = nickname,
        email = username,
        profileCompleted = profileCompleted,
        updatedAt = currentTimeMillis
    )
}

