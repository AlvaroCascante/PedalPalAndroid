package com.quetoquenana.and.util

import com.quetoquenana.and.features.auth.domain.model.BackendPerson
import com.quetoquenana.and.features.auth.domain.model.BackendUser
import com.quetoquenana.and.features.auth.domain.model.FirebaseUserInfo

val firebaseUserInfoVerified = FirebaseUserInfo(
    uid = "u1",
    email = "a@b.com",
    displayName = "X",
    isEmailVerified = true
)

val firebaseUserInfoUnverified = FirebaseUserInfo(
    uid = "u1",
    email = "s@example.com",
    displayName = "X",
    isEmailVerified = false
)

val backendPerson = BackendPerson(
    idNumber = "id-1",
    name = "John",
    lastname = "Doe"
)

val backendUser = BackendUser(
    username = "joe",
    nickname = "joe_nick",
    person = backendPerson
)
