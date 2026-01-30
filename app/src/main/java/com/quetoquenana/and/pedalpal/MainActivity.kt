package com.quetoquenana.and.pedalpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.pedalpal.core.ui.theme.PedalPalTheme
import dagger.hilt.android.AndroidEntryPoint
import com.quetoquenana.and.pedalpal.core.ui.navigation.ProvideNavigator
import com.quetoquenana.and.pedalpal.core.ui.navigation.AppNavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PedalPalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    val navController = rememberNavController()
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