package com.quetoquenana.and.pedalpal.core.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen(route = "login")
    object Home : Screen(route = "home")
    object CompleteProfile : Screen(route = "complete_profile")
}
