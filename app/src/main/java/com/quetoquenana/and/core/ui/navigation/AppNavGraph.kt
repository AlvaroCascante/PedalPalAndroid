package com.quetoquenana.and.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.quetoquenana.and.features.appointments.AddAppointmentScreen
import com.quetoquenana.and.features.appointments.AppointmentDetailRoute
import com.quetoquenana.and.features.appointments.AppointmentsRoute
import com.quetoquenana.and.features.authentication.ui.AuthenticationRoute
import com.quetoquenana.and.features.authentication.ui.CompleteProfileRoute
import com.quetoquenana.and.features.authentication.ui.StartupRoute
import com.quetoquenana.and.features.bikes.ui.AddBikeRoute
import com.quetoquenana.and.features.bikes.ui.BikeComponentRoute
import com.quetoquenana.and.features.bikes.ui.BikeDetailRoute
import com.quetoquenana.and.features.bikes.ui.BikeHistoryRoute
import com.quetoquenana.and.features.bikes.ui.BikeMediaRoute
import com.quetoquenana.and.features.bikes.ui.BikesRoute
import com.quetoquenana.and.features.bikes.ui.StravaImportRoute
import com.quetoquenana.and.features.home.ui.HomeRoute
import com.quetoquenana.and.features.profile.ui.ProfileRoute

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
            AppointmentsRoute(
                onAppointmentClick = { id -> navController.navigate(AppointmentDetail.createRoute(id)) },
                onAddAppointmentClick = { navController.navigate(AddAppointment.route) }
            )
        }

        composable(Bikes.route) {
            BikesRoute(
                onNavigateAddBike = { navController.navigate(AddBike.createRoute()) },
                onNavigateStravaImport = { navController.navigate(StravaImport.route) },
                onNavigateBikeDetail = { id -> navController.navigate(BikeDetail.createRoute(id)) }
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
                },
                navArgument("odometerKm") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("externalGearId") {
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
                prefillOdometerKm = backStackEntry.arguments?.getString("odometerKm"),
                prefillExternalGearId = backStackEntry.arguments?.getString("externalGearId"),
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
                            notes = notes,
                            odometerKm = bike.distance?.toInt()?.toString(),
                            externalGearId = bike.id
                        )
                    )
                }
            )
        }

        composable(Profile.route) {
            ProfileRoute(
                onLoggedOut = {
                    navController.navigate(Startup.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(CompleteProfile.route) {
            CompleteProfileRoute(onComplete = { navController.navigate(Home.route) })
        }

        composable(AppointmentDetail.route) {
            AppointmentDetailRoute()
        }

        composable(AddAppointment.route) {
            AddAppointmentScreen(
                onDone = {
                    navController.navigate(Appointments.route) {
                        popUpTo(AddAppointment.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(BikeDetail.route) {
            BikeDetailRoute(
                onNavigateHistory = { bikeId -> navController.navigate(BikeHistory.createRoute(bikeId)) },
                onNavigateBikeImages = { bikeId -> navController.navigate(BikeImages.createRoute(bikeId)) },
                onNavigateStravaSync = { navController.navigate(StravaImport.route) },
                onNavigateComponentOptions = { bikeId, componentId ->
                    navController.navigate(BikeComponent.createRoute(bikeId, componentId))
                }
            )
        }

        composable(BikeHistory.route) {
            BikeHistoryRoute()
        }

        composable(BikeImages.route) {
            BikeMediaRoute()
        }

        composable(BikeComponent.route) { backStackEntry ->
            val bikeId = backStackEntry.arguments?.getString("bikeId").orEmpty()
            BikeComponentRoute(
                bikeId = bikeId,
                componentId = backStackEntry.arguments?.getString("componentId").orEmpty(),
                onComponentSaved = { savedBikeId ->
                    navController.navigate(BikeDetail.createRoute(savedBikeId)) {
                        popUpTo(BikeDetail.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
