package com.quetoquenana.and.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.core.ui.navigation.Appointments
import com.quetoquenana.and.core.ui.navigation.Bikes
import com.quetoquenana.and.core.ui.navigation.Home
import com.quetoquenana.and.core.ui.navigation.Profile
import com.quetoquenana.and.core.ui.navigation.Screen
import com.quetoquenana.and.core.ui.navigation.bottomBarScreens

private fun iconFor(screen: com.quetoquenana.and.core.ui.navigation.Screen): ImageVector = when (screen) {
    is com.quetoquenana.and.core.ui.navigation.Home -> Icons.Default.Home
    is com.quetoquenana.and.core.ui.navigation.Bikes -> Icons.AutoMirrored.Filled.DirectionsBike
    is com.quetoquenana.and.core.ui.navigation.Appointments -> Icons.Default.Event
    is com.quetoquenana.and.core.ui.navigation.Profile -> Icons.Default.Person
    else -> Icons.Default.Home
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    appointmentsBadgeCount: Int = 0
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = modifier.fillMaxWidth()) {
        _root_ide_package_.com.quetoquenana.and.core.ui.navigation.bottomBarScreens.forEach { screen ->
            val selected = currentRoute == screen.route

            val icon: @Composable () -> Unit = {
                if (screen is com.quetoquenana.and.core.ui.navigation.Appointments && appointmentsBadgeCount > 0) {
                    BadgedBox(badge = { Badge { Text(text = appointmentsBadgeCount.toString()) } }) {
                        Icon(imageVector = iconFor(screen), contentDescription = screen.route)
                    }
                } else {
                    Icon(imageVector = iconFor(screen), contentDescription = screen.route)
                }
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = icon,
                label = { Text(text = screen.label) }
            )
        }
    }
}

@Preview
@Composable
private fun BottomBarPreview() {
    BottomBar(navController = rememberNavController(), appointmentsBadgeCount = 3)
}