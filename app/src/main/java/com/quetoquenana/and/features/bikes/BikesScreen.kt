package com.quetoquenana.and.features.bikes

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
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.components.LogoImage
import com.quetoquenana.and.core.ui.navigation.Bikes
import com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.core.ui.theme.PedalPalTheme


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
        _root_ide_package_.com.quetoquenana.and.core.ui.components.LogoImage()
        Text(text = "Bikes Screen")
    }
}

@Preview(showSystemUi = true)
@Composable
private fun BikesScreenContentPreview() {

    _root_ide_package_.com.quetoquenana.and.core.ui.theme.PedalPalTheme {
        val navController = rememberNavController()
        val currentRoute = _root_ide_package_.com.quetoquenana.and.core.ui.navigation.Bikes.route
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