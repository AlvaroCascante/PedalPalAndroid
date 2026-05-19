package com.quetoquenana.and.features.authentication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quetoquenana.and.features.authentication.domain.model.AuthUserResult

@Entity(tableName = "auth_user")
data class AuthUserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val lastname: String?,
    val idNumber: String?,
    val nickname: String?,
    val email: String?,
    val profileCompleted: Boolean,
    val updatedAt: Long
)

fun AuthUserResult.toEntity(id: String, currentTimeMillis: Long): AuthUserEntity {
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

