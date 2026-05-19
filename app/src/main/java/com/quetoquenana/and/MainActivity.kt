package com.quetoquenana.and

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.components.PersonalizedGreeting
import com.quetoquenana.and.core.ui.components.TopBarTitle
import com.quetoquenana.and.core.ui.navigation.AddAppointment
import com.quetoquenana.and.core.ui.navigation.AddBike
import com.quetoquenana.and.core.ui.navigation.AppNavGraph
import com.quetoquenana.and.core.ui.navigation.AppointmentDetail
import com.quetoquenana.and.core.ui.navigation.Appointments
import com.quetoquenana.and.core.ui.navigation.BikeComponent
import com.quetoquenana.and.core.ui.navigation.BikeDetail
import com.quetoquenana.and.core.ui.navigation.BikeHistory
import com.quetoquenana.and.core.ui.navigation.BikeImages
import com.quetoquenana.and.core.ui.navigation.Bikes
import com.quetoquenana.and.core.ui.navigation.MainViewModel
import com.quetoquenana.and.core.ui.navigation.ProvideNavigator
import com.quetoquenana.and.core.ui.navigation.routeMatches
import com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.core.ui.navigation.shouldShowTopBar
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PedalPalTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val mainViewModel: MainViewModel = viewModels<MainViewModel>().value
                val badgeCount by mainViewModel.appointmentsBadgeCount.collectAsState()
                val userDisplayName by mainViewModel.userDisplayName.collectAsState()

                val showBottomBar = shouldShowBottomBar(currentRoute)
                val showTopBar = shouldShowTopBar(currentRoute)
                val componentId = navBackStackEntry?.arguments?.getString("componentId")
                val topBarTitle = when {
                    routeMatches(currentRoute, AddAppointment.route) -> "Book Service"
                    routeMatches(currentRoute, AddBike.route) -> "New Bike"
                    routeMatches(currentRoute, BikeHistory.route) -> "Bike History"
                    routeMatches(currentRoute, BikeImages.route) -> "Bike Images"
                    routeMatches(currentRoute, BikeComponent.route) && componentId == "new" -> "New Component"
                    routeMatches(currentRoute, BikeDetail.route) -> "Bike Details"
                    routeMatches(currentRoute, Bikes.route) -> "My Bikes"
                    routeMatches(currentRoute, Appointments.route) -> "My Appointments"
                    routeMatches(currentRoute, AppointmentDetail.route) -> "Appointment Details"
                    else -> null
                }

                LaunchedEffect(showTopBar, topBarTitle) {
                    if (showTopBar && topBarTitle == null) {
                        mainViewModel.loadUserDisplayName()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (showTopBar) {
                            if (topBarTitle != null) {
                                TopBarTitle(
                                    title = topBarTitle,
                                    modifier = Modifier
                                        .statusBarsPadding()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            } else {
                                PersonalizedGreeting(
                                    name = userDisplayName,
                                    modifier = Modifier
                                        .statusBarsPadding()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    },
                    bottomBar = {
                        if (showBottomBar) {
                            BottomBar(
                                navController = navController,
                                appointmentsBadgeCount = badgeCount
                            )
                        }
                    }
                ) { paddingValues ->
                    ProvideNavigator(
                        navController = navController
                    ) {
                        AppNavGraph(
                            navController = navController,
                            modifier = Modifier
                                .padding(paddingValues = paddingValues)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
