package com.quetoquenana.and.pedalpal.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.quetoquenana.and.pedalpal.feature.appointments.AppointmentsScreen
import com.quetoquenana.and.pedalpal.feature.bikes.BikesScreen
import com.quetoquenana.and.pedalpal.feature.home.HomeScreen
import com.quetoquenana.and.pedalpal.feature.login.ui.CompleteProfileRoute
import com.quetoquenana.and.pedalpal.feature.login.ui.CompleteProfileScreen
import com.quetoquenana.and.pedalpal.feature.login.ui.LoginRoute
import com.quetoquenana.and.pedalpal.feature.profile.ProfileScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Login.route,
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Login.route) {
            LoginRoute(
                onNavigateHome = { navController.navigate(Home.route) },
                onNavigateCompleteProfile = { navController.navigate(CompleteProfile.route) }
            )
        }

        composable(Home.route) {
            HomeScreen()
        }

        composable(Appointments.route) {
            AppointmentsScreen()
        }

        composable(Bikes.route) {
            BikesScreen()
        }

        composable(Profile.route) {
            ProfileScreen()
        }

        composable(CompleteProfile.route) {
            CompleteProfileRoute(onComplete = { navController.navigate(Home.route) })
        }
    }
}
