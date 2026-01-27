package com.quetoquenana.and.pedalpal.feature.auth.domain.repository

import com.quetoquenana.and.pedalpal.feature.auth.domain.model.AuthToken

interface AuthRepository {
    suspend fun login(username: String, password: String): AuthToken
}