package com.quetoquenana.and.pedalpal.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.quetoquenana.and.pedalpal.feature.login.ui.LoginScreen
import com.quetoquenana.and.pedalpal.feature.home.HomeScreen
import com.quetoquenana.and.pedalpal.feature.login.ui.CompleteProfileScreen
import com.quetoquenana.and.pedalpal.feature.login.ui.LoginRoute

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Screen.Login.route) {
            LoginRoute(
                onNavigateHome = { navController.navigate(Screen.Home.route) },
                onNavigateCompleteProfile = { navController.navigate(Screen.CompleteProfile.route) }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.CompleteProfile.route) {
            CompleteProfileScreen(onComplete = { navController.navigate(Screen.Home.route) })
        }
    }
}
