package com.quetoquenana.and.pedalpal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.quetoquenana.and.pedalpal.feature.auth.ui.LoginScreen
import com.quetoquenana.and.pedalpal.feature.home.HomeScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateHome = { navController.navigate(Screen.Home.route) },
                onCreateAccountClick = {},
                onForgotPasswordClick = {}
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}
