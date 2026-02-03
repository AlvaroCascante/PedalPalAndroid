package com.quetoquenana.and.pedalpal.features.bikes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.pedalpal.core.ui.components.BottomBar
import com.quetoquenana.and.pedalpal.core.ui.components.LogoImage
import com.quetoquenana.and.pedalpal.core.ui.navigation.Bikes
import com.quetoquenana.and.pedalpal.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.pedalpal.core.ui.theme.PedalPalTheme


@Composable
fun BikesScreen(
    modifier: Modifier = Modifier,

) {
    BikesScreenContent(
        modifier = modifier
    )
}

@Composable
private fun BikesScreenContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LogoImage()
        Text(text = "Bikes Screen")
    }
}

@Preview(showSystemUi = true)
@Composable
private fun BikesScreenContentPreview() {

    PedalPalTheme {
        val navController = rememberNavController()
        val currentRoute = Bikes.route
        val showBottomBar = shouldShowBottomBar(currentRoute)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    BottomBar(
                        navController = navController,
                        appointmentsBadgeCount = 1
                    )
                }
            }
        ) { paddingValues ->
            BikesScreenContent(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize()
            )
        }
    }
}