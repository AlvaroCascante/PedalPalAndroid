package com.quetoquenana.and.features.authentication.domain.model

sealed interface CreateUserUseCaseResult {
    data class Success(val userId: String) : CreateUserUseCaseResult
    data object InvalidFirebaseSession : CreateUserUseCaseResult
    data object NetworkError : CreateUserUseCaseResult
    data object UnknownError : CreateUserUseCaseResult
}
