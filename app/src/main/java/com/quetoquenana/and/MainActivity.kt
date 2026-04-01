package com.quetoquenana.and

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.components.PersonalizedGreeting
import com.quetoquenana.and.core.ui.navigation.AppNavGraph
import com.quetoquenana.and.core.ui.navigation.MainViewModel
import com.quetoquenana.and.core.ui.navigation.ProvideNavigator
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

                val showBottomBar = shouldShowBottomBar(currentRoute)
                val showTopBar = shouldShowTopBar(currentRoute)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (showTopBar) {
                            PersonalizedGreeting(
                                name = "John Doe",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
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