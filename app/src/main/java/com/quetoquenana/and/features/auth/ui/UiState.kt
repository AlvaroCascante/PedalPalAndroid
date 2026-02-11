package com.quetoquenana.and.features.auth.ui

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isEmailVerificationSent: Boolean = false
)

data class CompleteProfileUiState(
    val nickname: String = "",
    val idNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val isSaving: Boolean = false
)