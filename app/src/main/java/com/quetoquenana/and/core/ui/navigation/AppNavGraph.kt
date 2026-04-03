package com.quetoquenana.and.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.quetoquenana.and.features.appointments.AddAppointmentScreen
import com.quetoquenana.and.features.appointments.AppointmentDetailScreen
import com.quetoquenana.and.features.appointments.AppointmentsScreen
import com.quetoquenana.and.features.authentication.ui.AuthenticationRoute
import com.quetoquenana.and.features.authentication.ui.CompleteProfileRoute
import com.quetoquenana.and.features.authentication.ui.StartupRoute
import com.quetoquenana.and.features.bikes.ui.AddBikeRoute
import com.quetoquenana.and.features.bikes.ui.BikesRoute
import com.quetoquenana.and.features.bikes.ui.StravaImportRoute
import com.quetoquenana.and.features.home.ui.HomeRoute
import com.quetoquenana.and.features.profile.ui.ProfileScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Startup.route,
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {

        composable(Startup.route) {
            StartupRoute(
                onNavigateHome = {
                    navController.navigate(Home.route) {
                        popUpTo(Startup.route) { inclusive = true }
                    }
                },
                onNavigateAuth = {
                    navController.navigate(Authentication.route) {
                        popUpTo(Startup.route) { inclusive = true }
                    }
                },
                onNavigateCompleteProfile = {
                    navController.navigate(CompleteProfile.route) {
                        popUpTo(Startup.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Authentication.route) {
            AuthenticationRoute(
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
            BikesRoute(
                onNavigateAddBike = { navController.navigate(AddBike.createRoute()) }
            )
        }

        composable(
            route = AddBike.route,
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("model") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("notes") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            AddBikeRoute(
                prefillName = backStackEntry.arguments?.getString("name"),
                prefillModel = backStackEntry.arguments?.getString("model"),
                prefillNotes = backStackEntry.arguments?.getString("notes"),
                onNavigateBikes = {
                    navController.navigate(Bikes.route) {
                        popUpTo(AddBike.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(StravaImport.route) {
            StravaImportRoute(
                onNavigateToCreateBike = { bike ->
                    val notes = buildString {
                        append("Imported from Strava")
                        bike.nickname?.let { append(" · nickname: $it") }
                        bike.distance?.let { append(" · distance: $it") }
                    }

                    navController.navigate(
                        AddBike.createRoute(
                            name = bike.name,
                            model = bike.nickname,
                            notes = notes
                        )
                    )
                }
            )
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
