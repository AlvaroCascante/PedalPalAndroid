package com.quetoquenana.and.pedalpal.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.pedalpal.R
import com.quetoquenana.and.pedalpal.core.ui.components.BottomBar
import com.quetoquenana.and.pedalpal.core.ui.components.LogoImage
import com.quetoquenana.and.pedalpal.core.ui.navigation.Home
import com.quetoquenana.and.pedalpal.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.pedalpal.core.ui.theme.PedalPalTheme


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,

) {
    HomeScreenContent(
        modifier = modifier
    )
}

@Composable
private fun HomeScreenContent(
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
        Text(text = "Home Screen")
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenContentPreview() {

    PedalPalTheme {
        val navController = rememberNavController()
        val currentRoute = Home.route
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
            HomeScreenContent(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize()
            )
        }
    }
}