package com.quetoquenana.and.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.quetoquenana.and.core.utils.ARG_FROM_DEEPLINK
import com.quetoquenana.and.core.utils.SCREEN_PARAM_BIKE_ID
import com.quetoquenana.and.core.utils.NAV_ARG_BRAND
import com.quetoquenana.and.core.utils.SCREEN_PARAM_COMPONENT_ID
import com.quetoquenana.and.core.utils.NAV_ARG_EXTERNAL_GEAR_ID
import com.quetoquenana.and.core.utils.NAV_ARG_MODEL
import com.quetoquenana.and.core.utils.NAV_ARG_NAME
import com.quetoquenana.and.core.utils.NAV_ARG_NOTES
import com.quetoquenana.and.core.utils.NAV_ARG_ODOMETER
import com.quetoquenana.and.features.appointments.ui.AddAppointmentScreen
import com.quetoquenana.and.features.appointments.ui.AppointmentDetailRoute
import com.quetoquenana.and.features.appointments.ui.AppointmentsRoute
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
import java.util.UUID

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Startup.route,
    username: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Startup.route) {
            StartupRoute(
                onNavigateHome = {
                    navController.navigate(route = Home.route) {
                        popUpTo(route = Startup.route) { inclusive = true }
                    }
                },
                onNavigateAuth = {
                    navController.navigate(route = Authentication.route) {
                        popUpTo(route = Startup.route) { inclusive = true }
                    }
                },
                onNavigateCompleteProfile = {
                    navController.navigate(route = CompleteProfile.route) {
                        popUpTo(route = Startup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Authentication.route) {
            AuthenticationRoute(
                onNavigateHome = { navController.navigate(route = Home.route) },
                onNavigateCompleteProfile = { navController.navigate(route = CompleteProfile.route) }
            )
        }

        composable(route = Home.route) {
            HomeRoute(name = username)
        }

        composable(route = Appointments.route) {
            AppointmentsRoute(
                onAppointmentClick = { id -> navController.navigate(route = AppointmentDetail.createRoute(id = id)) },
                onAddAppointmentClick = { navController.navigate(route = AddAppointment.route) }
            )
        }

        composable(route = Bikes.route) {
            BikesRoute(
                onNavigateAddBike = { navController.navigate(route = AddBike.createRoute()) },
                onNavigateStravaImport = { navController.navigate(route = StravaImport.createRoute()) },
                onNavigateBikeDetail = { id -> navController.navigate(route = BikeDetail.createRoute(id = id)) }
            )
        }

        composable(
            route = AddBike.route,
            arguments = AddBike.arguments
        ) { backStackEntry ->
            AddBikeRoute(
                prefillName = backStackEntry.arguments?.getString(NAV_ARG_NAME),
                prefillBrand = backStackEntry.arguments?.getString(NAV_ARG_BRAND),
                prefillModel = backStackEntry.arguments?.getString(NAV_ARG_MODEL),
                prefillNotes = backStackEntry.arguments?.getString(NAV_ARG_NOTES),
                prefillOdometerKm = backStackEntry.arguments?.getString(NAV_ARG_ODOMETER),
                prefillExternalGearId = backStackEntry.arguments?.getString(NAV_ARG_EXTERNAL_GEAR_ID),
                onNavigateBikes = {
                    navController.navigate(route = Bikes.route) {
                        popUpTo(route = AddBike.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = StravaImport.route,
            arguments = listOf(
                navArgument(name = ARG_FROM_DEEPLINK) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val fromDeepLink = backStackEntry.arguments?.getBoolean(ARG_FROM_DEEPLINK) ?: false

            StravaImportRoute(
                onNavigateToCreateBike = { bike ->
                    // TODO Handle these values internalized
                    val notes = buildString {
                        append("Imported from Strava")
                        bike.nickname?.let { append(" · nickname: $it") }
                        bike.distance?.let { append(" · distance: $it") }
                    }

                    navController.navigate(
                        route = AddBike.createRoute(
                            name = bike.name,
                            notes = notes,
                            odometerKm = bike.distance?.toInt()?.toString(),
                            externalGearId = bike.id
                        )
                    )
                },
                fromDeepLink = fromDeepLink
            )
        }

        composable(route = Profile.route) {
            ProfileRoute(
                onLoggedOut = {
                    navController.navigate(route = Startup.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = CompleteProfile.route) {
            CompleteProfileRoute(onComplete = { navController.navigate(route = Home.route) })
        }

        composable(route = AppointmentDetail.route) {
            AppointmentDetailRoute()
        }

        composable(route = AddAppointment.route) {
            AddAppointmentScreen(
                onDone = {
                    navController.navigate(route = Appointments.route) {
                        popUpTo(route = AddAppointment.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = BikeDetail.route) {
            BikeDetailRoute(
                onNavigateHistory = { bikeId -> navController.navigate(route = BikeHistory.createRoute(id = bikeId)) },
                onNavigateBikeImages = { bikeId -> navController.navigate(route = BikeImages.createRoute(id = bikeId)) },
                onNavigateStravaSync = { navController.navigate(route = StravaImport.createRoute()) },
                onNavigateComponentOptions = { bikeId, componentId ->
                    navController.navigate(route = BikeComponent.createRoute(bikeId = bikeId, componentId = componentId))
                }
            )
        }

        composable(route = BikeHistory.route) {
            BikeHistoryRoute()
        }

        composable(route = BikeImages.route) {
            BikeMediaRoute()
        }

        composable(route = BikeComponent.route) { backStackEntry ->
            val bikeId: UUID = backStackEntry.arguments?.getString(SCREEN_PARAM_BIKE_ID)
                ?.let { UUID.fromString(it) }
                ?: throw IllegalArgumentException("bikeId is required and must be a valid UUID")
            BikeComponentRoute(
                bikeId = bikeId,
                componentId = backStackEntry.arguments?.getString(SCREEN_PARAM_COMPONENT_ID).orEmpty(),
                onComponentSaved = { savedBikeId ->
                    navController.navigate(route = BikeDetail.createRoute(savedBikeId)) {
                        popUpTo(route = BikeDetail.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
