package com.quetoquenana.and.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.quetoquenana.and.features.appointments.AddAppointmentScreen
import com.quetoquenana.and.features.appointments.AppointmentDetailScreen
import com.quetoquenana.and.features.appointments.AddAppointmentScreen
import com.quetoquenana.and.features.appointments.AppointmentDetailScreen
import com.quetoquenana.and.features.appointments.AppointmentsScreen
import com.quetoquenana.and.features.auth.ui.AuthRoute
import com.quetoquenana.and.features.auth.ui.CompleteProfileRoute
import com.quetoquenana.and.features.bikes.BikesScreen
import com.quetoquenana.and.features.home.ui.HomeRoute
import com.quetoquenana.and.features.profile.ProfileScreen
import com.quetoquenana.and.features.auth.ui.AuthRoute
import com.quetoquenana.and.features.auth.ui.CompleteProfileRoute
import com.quetoquenana.and.features.bikes.BikesScreen
import com.quetoquenana.and.features.home.ui.HomeRoute
import com.quetoquenana.and.features.profile.ProfileScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Login.route,
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Login.route) {
            AuthRoute(
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
