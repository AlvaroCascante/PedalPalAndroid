package com.quetoquenana.and.pedalpal.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.quetoquenana.and.pedalpal.features.appointments.AppointmentDetailScreen
import com.quetoquenana.and.pedalpal.features.appointments.AddAppointmentScreen
import com.quetoquenana.and.pedalpal.features.appointments.AppointmentsScreen
import com.quetoquenana.and.pedalpal.features.bikes.BikesScreen
import com.quetoquenana.and.pedalpal.features.home.ui.HomeRoute
import com.quetoquenana.and.pedalpal.features.login.ui.CompleteProfileRoute
import com.quetoquenana.and.pedalpal.features.login.ui.LoginRoute
import com.quetoquenana.and.pedalpal.features.profile.ProfileScreen

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
            HomeRoute()
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

        composable(AppointmentDetail.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            AppointmentDetailScreen(appointmentId = id)
        }

        composable(AddAppointment.route) {
            AddAppointmentScreen()
        }
    }
}
