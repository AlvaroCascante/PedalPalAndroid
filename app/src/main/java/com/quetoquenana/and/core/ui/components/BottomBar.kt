package com.quetoquenana.and.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.navigation.Appointments
import com.quetoquenana.and.core.ui.navigation.Bikes
import com.quetoquenana.and.core.ui.navigation.Home
import com.quetoquenana.and.core.ui.navigation.Profile
import com.quetoquenana.and.core.ui.navigation.Screen
import com.quetoquenana.and.core.ui.navigation.bottomBarScreens

private fun iconFor(screen: Screen): ImageVector = when (screen) {
    is Home -> Icons.Default.Home
    is Bikes -> Icons.AutoMirrored.Filled.DirectionsBike
    is Appointments -> Icons.Default.Event
    is Profile -> Icons.Default.Person
    else -> Icons.Default.Home
}

@Composable
fun TopBar(
    modifier: Modifier
) {
    Column(
        modifier = modifier.background(color = MaterialTheme.colorScheme.onPrimary),
        horizontalAlignment = CenterHorizontally
    ) {
        PersonalizedGreeting(
            name = "John Doe",
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
fun PersonalizedGreeting(
    name: String?,
    modifier: Modifier = Modifier
) {
    val greeting = if (name != null) {
        "Welcome back, $name!"
    } else {
        "Welcome to PedalPal!"
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
        verticalAlignment  = CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Greeting Icon"
        )
        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
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
        bottomBarScreens.forEach { screen ->
            val selected = currentRoute == screen.route

            val icon: @Composable () -> Unit = {
                if (screen is Appointments && appointmentsBadgeCount > 0) {
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
