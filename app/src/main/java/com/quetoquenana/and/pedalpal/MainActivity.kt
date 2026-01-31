package com.quetoquenana.and.pedalpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.pedalpal.core.ui.theme.PedalPalTheme
import dagger.hilt.android.AndroidEntryPoint
import com.quetoquenana.and.pedalpal.core.ui.navigation.ProvideNavigator
import com.quetoquenana.and.pedalpal.core.ui.navigation.AppNavGraph
import com.quetoquenana.and.pedalpal.core.ui.components.BottomBar
import com.quetoquenana.and.pedalpal.core.ui.navigation.MainViewModel
import com.quetoquenana.and.pedalpal.core.ui.navigation.shouldShowBottomBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState

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

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomBar(navController = navController, appointmentsBadgeCount = badgeCount)
                        }
                    }
                ) { paddingValues ->
                    ProvideNavigator(navController = navController) {
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