package com.quetoquenana.and.features.profile.domain.model

data class Profile(
    val id: String,
    val name: String,
    val lastname: String?,
    val idNumber: String?,
    val username: String?,
    val externalId: String?,
    val provider: String?,
    val nickname: String?,
    val userStatus: String?,
    val photoUrl: String?,
    val profileMediaId: String?,
)

