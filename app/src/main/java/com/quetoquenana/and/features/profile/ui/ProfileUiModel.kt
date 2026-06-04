package com.quetoquenana.and.features.profile.ui

import com.quetoquenana.and.features.profile.domain.model.Profile
import java.util.UUID

data class ProfileUiModel(
    val id: UUID,
    val name: String,
    val lastname: String,
    val idNumber: String,
    val username: String,
    val nickname: String,
    val photoUrl: String? = null
)

fun Profile.toUiModel(): ProfileUiModel {
    return ProfileUiModel(
        id = id,
        name = name,
        lastname = lastname.orEmpty(),
        idNumber = idNumber.orEmpty(),
        username = username.orEmpty(),
        nickname = nickname.orEmpty(),
        photoUrl = profileImageUrl
    )
}

