package com.quetoquenana.and.features.authentication.domain.model

import java.util.UUID

sealed interface CreateUserUseCaseResult {
    data class Success(val userId: UUID) : CreateUserUseCaseResult
    data object InvalidFirebaseSession : CreateUserUseCaseResult
    data object NetworkError : CreateUserUseCaseResult
    data object UnknownError : CreateUserUseCaseResult
}
