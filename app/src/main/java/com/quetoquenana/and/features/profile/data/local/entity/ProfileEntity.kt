package com.quetoquenana.and.features.profile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quetoquenana.and.features.profile.domain.model.Profile

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val lastname: String?,
    val idNumber: String?,
    val username: String?,
    val externalId: String?,
    val provider: String?,
    val nickname: String?,
    val userStatus: String?,
    val profileMediaId: String?,
    val updatedAt: Long,
)

fun ProfileEntity.toDomain(photoUrl: String?): Profile {
    return Profile(
        id = id,
        name = name,
        lastname = lastname,
        idNumber = idNumber,
        username = username,
        externalId = externalId,
        provider = provider,
        nickname = nickname,
        userStatus = userStatus,
        photoUrl = photoUrl,
        profileMediaId = profileMediaId,
    )
}

fun Profile.toEntity(currentTimeMillis: Long): ProfileEntity {
    return ProfileEntity(
        id = id,
        name = name,
        lastname = lastname,
        idNumber = idNumber,
        username = username,
        externalId = externalId,
        provider = provider,
        nickname = nickname,
        userStatus = userStatus,
        profileMediaId = profileMediaId,
        updatedAt = currentTimeMillis,
    )
}

