package com.quetoquenana.and.pedalpal.core.ui.navigation

import androidx.navigation.NavHostController

class NavControllerNavigator(private val navController: NavHostController) : Navigator {
    override fun navigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    override fun navigateUp(): Boolean = navController.navigateUp()

    override fun navigateAndClearBackstack(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }
}
