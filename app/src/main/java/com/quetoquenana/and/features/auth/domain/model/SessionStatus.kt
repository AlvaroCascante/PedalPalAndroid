package com.quetoquenana.and.features.auth.domain.model

sealed interface SessionStatus {
    data object Authenticated : SessionStatus
    data object Unauthenticated : SessionStatus
    data object ProfileCompletionRequired : SessionStatus
}