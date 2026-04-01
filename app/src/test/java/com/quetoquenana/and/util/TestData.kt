package com.quetoquenana.and.util

import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel

val firebaseUserInfoVerified = FirebaseUserModel(
    uid = "u1",
    email = "a@b.com",
    displayName = "X",
    isEmailVerified = true
)

val firebaseUserInfoUnverified = FirebaseUserModel(
    uid = "u1",
    email = "s@example.com",
    displayName = "X",
    isEmailVerified = false
)
