package com.quetoquenana.and.features.profile.data.remote.dto

import com.quetoquenana.and.features.profile.domain.model.Profile

data class ProfileResponseDto(
    val id: String,
    val person: ProfilePersonResponseDto,
    val username: String,
    val externalId: String?,
    val provider: String?,
    val nickname: String?,
    val userStatus: String?,
)

data class ProfilePersonResponseDto(
    val idNumber: String?,
    val name: String,
    val lastname: String?,
)

fun ProfileResponseDto.toDomain(
    photoUrl: String?,
    profileMediaId: String?,
): Profile {
    return Profile(
        id = id,
        name = person.name,
        lastname = person.lastname,
        idNumber = person.idNumber,
        username = username,
        externalId = externalId,
        provider = provider,
        nickname = nickname,
        userStatus = userStatus,
        photoUrl = photoUrl,
        profileMediaId = profileMediaId,
    )
}



