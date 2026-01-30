package com.quetoquenana.and.pedalpal.feature.login.ui

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isEmailVerificationSent: Boolean = false
)