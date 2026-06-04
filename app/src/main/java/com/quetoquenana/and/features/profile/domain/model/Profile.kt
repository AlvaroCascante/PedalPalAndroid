package com.quetoquenana.and.features.profile.domain.model

import java.util.UUID

data class Profile(
    val id: UUID,
    val name: String,
    val lastname: String?,
    val idNumber: String?,
    val username: String?,
    val nickname: String?,
    val profileImageUrl: String? = null
)

