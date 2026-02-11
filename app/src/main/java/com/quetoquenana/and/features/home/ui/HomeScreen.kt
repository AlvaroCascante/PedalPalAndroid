package com.quetoquenana.and.features.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.AnimatedColoredShadows
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.components.BoxSurface
import com.quetoquenana.and.core.ui.components.LogoImage
import com.quetoquenana.and.core.ui.components.previewAppointments
import com.quetoquenana.and.core.ui.components.previewLandingPageItem
import com.quetoquenana.and.core.ui.components.previewSuggestion
import com.quetoquenana.and.core.ui.navigation.AddAppointment
import com.quetoquenana.and.core.ui.navigation.AppointmentDetail
import com.quetoquenana.and.core.ui.navigation.Home
import com.quetoquenana.and.core.ui.navigation.LocalNavigator
import com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.landing.domain.model.LandingPageItem
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val navigator = _root_ide_package_.com.quetoquenana.and.core.ui.navigation.LocalNavigator.current

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onAppointmentClick = { id -> navigator.navigate(route = _root_ide_package_.com.quetoquenana.and.core.ui.navigation.AppointmentDetail.createRoute(id)) },
        onEmptyClick = { navigator.navigate(_root_ide_package_.com.quetoquenana.and.core.ui.navigation.AddAppointment.route) }
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    snackBarHostState: SnackbarHostState,
    onAppointmentClick: (String) -> Unit = {},
    onEmptyClick: () -> Unit = {}
) {

    HomeScreenContent(
        modifier = modifier,
        appointments = uiState.appointments,
        suggestions = uiState.suggestions,
        landingItems = uiState.landingItems,
        onAppointmentClick = onAppointmentClick,
        onEmptyClick = onEmptyClick
    )

    SnackbarHost(
        hostState = snackBarHostState
    )
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    appointments: List<com.quetoquenana.and.features.appointments.domain.model.Appointment> = emptyList(),
    suggestions: List<com.quetoquenana.and.features.suggestions.domain.model.Suggestion> = emptyList(),
    landingItems: List<com.quetoquenana.and.features.landing.domain.model.LandingPageItem> = emptyList(),
    onAppointmentClick: (String) -> Unit = {},
    onEmptyClick: () -> Unit = {}
) {
    // Use LazyColumn for vertical scroll
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        content = {
            item {
                Text(text = "Upcoming Appointments", style = MaterialTheme.typography.titleMedium)
                AppointmentsRow(
                    appointments = appointments,
                    onAppointmentClick = onAppointmentClick,
                    onEmptyClick = onEmptyClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(height = 20.dp))
                Text(text = "Suggestions for you", style = MaterialTheme.typography.titleMedium)
                SuggestionsRow(
                    suggestions = suggestions,
                    onSuggestionClick = { /* placeholder - navigate */ },
                    onEmptyClick = { /* placeholder - navigate to suggestions */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(height = 20.dp))
                Text(text = "Explore", style = MaterialTheme.typography.titleMedium)
            }

            items(landingItems, key = { it.id }) { landing ->
                LandingCard(item = landing, onClick = { /* placeholder */ })
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(height = 20.dp))
                _root_ide_package_.com.quetoquenana.and.core.ui.components.LogoImage()
            }
        }
    )
}

@Composable
fun AppointmentCard(
    appointment: com.quetoquenana.and.features.appointments.domain.model.Appointment,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    _root_ide_package_.com.quetoquenana.and.core.ui.components.AnimatedColoredShadows(
        content = {
            Column(
                modifier = modifier
                    .size(width = 160.dp, height = 100.dp)
                    .clickable(onClick = onClick)
                    .padding(all = 12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start

            ) {
                val res = appointment.thumbnailRes ?: R.drawable.mobi_bike_logo
                Image(
                    painter = painterResource(id = res),
                    contentDescription = null,
                    modifier = Modifier.size(size = 40.dp)
                )

                Text(
                    text = appointment.dateText,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = appointment.bikeName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}

@Composable
fun AppointmentsRow(
    modifier: Modifier = Modifier,
    appointments: List<com.quetoquenana.and.features.appointments.domain.model.Appointment>,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    onAppointmentClick: (String) -> Unit = {},
    onEmptyClick: () -> Unit = {}
) {
    if (appointments.isEmpty()) {
        // Empty state card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 120.dp)
                .clickable(onClick = onEmptyClick),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No appointments yet", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Add your first appointment",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        return
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        contentPadding = contentPadding
    ) {
        items(items = appointments, key = { it.id }) { appointment ->
            AppointmentCard(appointment = appointment, onClick = { onAppointmentClick(appointment.id) })
        }
    }
}

@Composable
fun SuggestionCard(
    suggestion: com.quetoquenana.and.features.suggestions.domain.model.Suggestion,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .size(width = 160.dp, height = 100.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .padding(all = 12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            val res = suggestion.thumbnailRes ?: R.drawable.mobi_bike_logo
            Image(
                painter = painterResource(id = res),
                contentDescription = null,
                modifier = Modifier.size(size = 36.dp)
            )

            Text(
                text = suggestion.title,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = suggestion.subtitle,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SuggestionsRow(
    modifier: Modifier = Modifier,
    suggestions: List<com.quetoquenana.and.features.suggestions.domain.model.Suggestion>,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    onSuggestionClick: (String) -> Unit = {},
    onEmptyClick: () -> Unit = {}
) {
    if (suggestions.isEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 96.dp)
                .clickable(onClick = onEmptyClick),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(all = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No suggestions right now", style = MaterialTheme.typography.titleMedium)
                Text(text = "Explore bikes and offers", style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        contentPadding = contentPadding
    ) {
        items(items = suggestions, key = { it.id }) { suggestion ->
            SuggestionCard(suggestion = suggestion, onClick = { onSuggestionClick(suggestion.id) })
        }
    }
}

@Composable
fun LandingCard(
    item: com.quetoquenana.and.features.landing.domain.model.LandingPageItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenContentPreview() {
    _root_ide_package_.com.quetoquenana.and.core.ui.theme.PedalPalTheme {
        val navController = rememberNavController()
        val currentRoute = _root_ide_package_.com.quetoquenana.and.core.ui.navigation.Home.route
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
            HomeScreenContent(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize(),
                appointments = remember { _root_ide_package_.com.quetoquenana.and.core.ui.components.previewAppointments },
                suggestions = remember { _root_ide_package_.com.quetoquenana.and.core.ui.components.previewSuggestion },
                landingItems = remember { _root_ide_package_.com.quetoquenana.and.core.ui.components.previewLandingPageItem }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenContentPreview_Empty() {
    _root_ide_package_.com.quetoquenana.and.core.ui.theme.PedalPalTheme {
        val navController = rememberNavController()
        val currentRoute = _root_ide_package_.com.quetoquenana.and.core.ui.navigation.Home.route
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
            HomeScreenContent(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize(),
                appointments = remember { emptyList() },
                suggestions = remember { emptyList() },
                landingItems = remember { emptyList() }
            )
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun BoxSurfacePreview_Empty() {
    _root_ide_package_.com.quetoquenana.and.core.ui.components.AnimatedColoredShadows(
        content = {
            Column(
                modifier = Modifier.padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No appointments yet", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Add your first appointment",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}


@Preview(showSystemUi = true)
@Composable
fun BoxSurfacePreview() {
    _root_ide_package_.com.quetoquenana.and.core.ui.components.AnimatedColoredShadows(
        content = {
            AppointmentCard(
                appointment = _root_ide_package_.com.quetoquenana.and.features.appointments.domain.model.Appointment(
                    id = "1",
                    bikeId = "bike_123",
                    bikeName = "Mountain Bike X200",
                    dateText = "Aug 25, 2024 - 10:00 AM",
                    thumbnailRes = null
                ),
                onClick = {}
            )
        }
    )
}