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
    val nickname: String?
)

fun ProfileEntity.toDomain(
    profileImageUrl: String? = null
): Profile {
    return Profile(
        id = id,
        name = name,
        lastname = lastname,
        idNumber = idNumber,
        username = username,
        nickname = nickname,
        profileImageUrl = profileImageUrl
    )
}

fun Profile.toEntity(): ProfileEntity {
    return ProfileEntity(
        id = id,
        name = name,
        lastname = lastname,
        idNumber = idNumber,
        username = username,
        nickname = nickname
    )
}

