package com.quetoquenana.and.features.authentication.domain.model

sealed interface SessionStatus {
    data object Authenticated : SessionStatus
    data object Unauthenticated : SessionStatus
    data object ProfileCompletionRequired : SessionStatus
}