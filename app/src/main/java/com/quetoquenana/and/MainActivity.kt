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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _root_ide_package_.com.quetoquenana.and.core.ui.theme.PedalPalTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val mainViewModel: com.quetoquenana.and.core.ui.navigation.MainViewModel =
                    viewModels<com.quetoquenana.and.core.ui.navigation.MainViewModel>().value
                val badgeCount by mainViewModel.appointmentsBadgeCount.collectAsState()

                val showBottomBar =
                    _root_ide_package_.com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar(
                        currentRoute
                    )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            _root_ide_package_.com.quetoquenana.and.core.ui.components.BottomBar(
                                navController = navController,
                                appointmentsBadgeCount = badgeCount
                            )
                        }
                    }
                ) { paddingValues ->
                    _root_ide_package_.com.quetoquenana.and.core.ui.navigation.ProvideNavigator(
                        navController = navController
                    ) {
                        _root_ide_package_.com.quetoquenana.and.core.ui.navigation.AppNavGraph(
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