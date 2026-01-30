package com.quetoquenana.and.pedalpal.core.ui.navigation

interface Navigator {
    fun navigateAndClearBackstack(route: String)
    fun navigateUp(): Boolean
    fun navigate(route: String)
}