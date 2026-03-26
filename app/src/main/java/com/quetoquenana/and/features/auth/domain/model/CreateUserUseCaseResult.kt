package com.quetoquenana.and.features.auth.domain.model

sealed interface CreateUserUseCaseResult {
    data class Success(val userId: String) : CreateUserUseCaseResult
    data object InvalidFirebaseSession : CreateUserUseCaseResult
    data object NetworkError : CreateUserUseCaseResult
    data object UnknownError : CreateUserUseCaseResult
}
