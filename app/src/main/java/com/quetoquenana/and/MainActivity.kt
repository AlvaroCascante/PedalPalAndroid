package com.quetoquenana.and

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.navigation.AppNavGraph
import com.quetoquenana.and.core.ui.navigation.DeepLinkRouter
import com.quetoquenana.and.core.ui.navigation.MainViewModel
import com.quetoquenana.and.core.ui.navigation.ProvideNavigator
import com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Shared flow used to deliver incoming deep-link URIs into Compose/nav layer
    private val deepLinkEvents = MutableSharedFlow<Uri?>(replay = 1)
    val deepLinkFlow = deepLinkEvents.asSharedFlow()

    @Inject
    lateinit var deepLinkRouter: DeepLinkRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // emit initial intent data (cold start)
        deepLinkEvents.tryEmit(intent?.data)

        enableEdgeToEdge()

        setContent {
            PedalPalTheme {
                val navController = rememberNavController()
                // collect deep link events and navigate when one arrives
                LaunchedEffect(key1 = navController) {
                    this@MainActivity.deepLinkFlow.collect { uri ->
                        val route = deepLinkRouter.parse(uri) ?: return@collect
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    }
                }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val mainViewModel: MainViewModel = viewModels<MainViewModel>().value
                val badgeCount by mainViewModel.appointmentsBadgeCount.collectAsState()
                val userDisplayName by mainViewModel.userDisplayName.collectAsState()

                val snackBarHostState = remember { SnackbarHostState() }
                val showBottomBar = shouldShowBottomBar(currentRoute)

                Scaffold(
                    contentWindowInsets = WindowInsets.safeContent,
                    snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                    bottomBar = {
                        if (showBottomBar) {
                            BottomBar(
                                navController = navController,
                                appointmentsBadgeCount = badgeCount
                            )
                        }
                    }
                ) { scaffoldPadding ->
                    ProvideNavigator(
                        navController = navController
                    ) {
                        AppNavGraph(
                            username = userDisplayName ?: "",
                            navController = navController,
                            contentPadding = scaffoldPadding
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // emit new deep link intents into the flow so Compose/nav can react
        deepLinkEvents.tryEmit(value = intent.data)
    }
}
