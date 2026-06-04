package com.quetoquenana.and.features.appointments.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.core.ui.components.previewAppointments
import com.quetoquenana.and.core.ui.navigation.Appointments
import com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import java.util.UUID

@Composable
fun AppointmentsRoute(
    modifier: Modifier = Modifier,
    onAppointmentClick: (UUID) -> Unit,
    onAddAppointmentClick: () -> Unit,
    viewModel: AppointmentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackBarHostState.showSnackbar(it) }
    }

    AppointmentsScreen(
        modifier = modifier,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onBikeFilterSelected = viewModel::onBikeFilterSelected,
        onAppointmentClick = onAppointmentClick,
        onAddAppointmentClick = onAddAppointmentClick
    )
}

@Composable
fun AppointmentsScreen(
    modifier: Modifier = Modifier,
    uiState: AppointmentsUiState,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    onBikeFilterSelected: (UUID?) -> Unit = {},
    onAppointmentClick: (UUID) -> Unit = {},
    onAddAppointmentClick: () -> Unit = {}
) {
    val shouldShowStickyBookService = !uiState.isLoading && uiState.upcomingAppointments.isNotEmpty()

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            if (shouldShowStickyBookService) {
                StickyBottomCta(
                    text = "Book service",
                    onClick = onAddAppointmentClick
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AppointmentsBanner()
            }

            item {
                BikeFilterChips(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    filters = uiState.bikeFilters,
                    selectedBikeId = uiState.selectedBikeId,
                    onBikeFilterSelected = onBikeFilterSelected
                )
            }

            item {
                when {
                    uiState.isLoading -> Text(
                        text = "Loading appointments...",
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    else -> AppointmentsTabs(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        upcomingAppointments = uiState.upcomingAppointments,
                        pastAppointments = uiState.pastAppointments,
                        selectedBikeName = uiState.selectedBikeName,
                        onAppointmentClick = onAppointmentClick,
                        onAddAppointmentClick = onAddAppointmentClick
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentsBanner(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.bn_apt_pop),
        contentDescription = "Appointments banner",
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        contentScale = ContentScale.Crop
    )
}

private enum class AppointmentsTab(val title: String) {
    Upcoming("Upcoming"),
    Past("Past")
}

@Composable
private fun BikeFilterChips(
    modifier: Modifier = Modifier,
    filters: List<AppointmentBikeFilter>,
    selectedBikeId: UUID?,
    onBikeFilterSelected: (UUID?) -> Unit
) {
    if (filters.isEmpty()) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item(key = "all-bikes") {
            FilterChip(
                selected = selectedBikeId == null,
                onClick = { onBikeFilterSelected(null) },
                label = { Text(text = "All bikes") }
            )
        }

        items(filters, key = { it.bikeId }) { filter ->
            FilterChip(
                selected = selectedBikeId?.equals(filter.bikeId) == true,
                onClick = { onBikeFilterSelected(filter.bikeId) },
                label = {
                    Text(
                        text = filter.bikeName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
private fun AppointmentsTabs(
    modifier: Modifier = Modifier,
    upcomingAppointments: List<Appointment>,
    pastAppointments: List<Appointment>,
    selectedBikeName: String?,
    onAppointmentClick: (UUID) -> Unit,
    onAddAppointmentClick: () -> Unit
) {
    var currentTab by rememberSaveable { mutableStateOf(AppointmentsTab.Upcoming) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PrimaryTabRow(selectedTabIndex = currentTab.ordinal) {
            AppointmentsTab.entries.forEach { tab ->
                Tab(
                    selected = currentTab == tab,
                    onClick = { currentTab = tab },
                    text = { Text(text = tab.title) }
                )
            }
        }

        when (currentTab) {
            AppointmentsTab.Upcoming -> {
                if (upcomingAppointments.isEmpty()) {
                    EmptyAppointmentsCard(
                        selectedBikeName = selectedBikeName,
                        onClick = onAddAppointmentClick
                    )
                } else {
                    AppointmentsList(
                        appointments = upcomingAppointments,
                        actionHint = "Tap to view selected services",
                        onAppointmentClick = onAppointmentClick
                    )
                }
            }

            AppointmentsTab.Past -> {
                if (pastAppointments.isEmpty()) {
                    Text(
                        text = selectedBikeName?.let { "No previous services for $it yet." }
                            ?: "No previous appointment history yet.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    AppointmentsList(
                        appointments = pastAppointments,
                        actionHint = "Work details will be available from service orders",
                        onAppointmentClick = onAppointmentClick
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentsList(
    appointments: List<Appointment>,
    actionHint: String,
    onAppointmentClick: (UUID) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        appointments.forEach { appointment ->
            AppointmentSummaryCard(
                appointment = appointment,
                modifier = Modifier.fillMaxWidth(),
                actionHint = actionHint,
                onClick = { onAppointmentClick(appointment.id) }
            )
        }
    }
}

@Composable
private fun EmptyAppointmentsCard(
    selectedBikeName: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "No appointments yet",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = selectedBikeName?.let { "Book a service visit for $it." }
                    ?: "Book a service visit for one of your bikes.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun AppointmentsScreenContentPreview() {
    PedalPalTheme {
        val navController = rememberNavController()
        val currentRoute = Appointments.route
        val showBottomBar = shouldShowBottomBar(currentRoute)
        val state = AppointmentsUiState(
            appointments = previewAppointments,
            bikeFilters = previewAppointments
                .distinctBy { it.bikeId }
                .map { AppointmentBikeFilter(it.bikeId, it.bikeName) }
        )

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
            AppointmentsScreen(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize(),
                uiState = state
            )
        }
    }
}
