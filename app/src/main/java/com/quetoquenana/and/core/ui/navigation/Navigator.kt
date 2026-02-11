package com.quetoquenana.and.core.ui.navigation

interface Navigator {
    fun navigateAndClearBackstack(route: String)
    fun navigateUp(): Boolean
    fun navigate(route: String)
}