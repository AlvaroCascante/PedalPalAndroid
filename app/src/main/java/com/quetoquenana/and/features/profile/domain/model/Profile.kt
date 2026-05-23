package com.quetoquenana.and.features.profile.domain.model

data class Profile(
    val id: String,
    val name: String,
    val lastname: String?,
    val idNumber: String?,
    val username: String?,
    val nickname: String?,
    val profileImageUrl: String? = null
)

