package com.quetoquenana.and.features.appointments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.components.previewAppointments
import com.quetoquenana.and.core.ui.navigation.Appointments
import com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.appointments.domain.model.Appointment

@Composable
fun AppointmentsRoute(
    modifier: Modifier = Modifier,
    onAppointmentClick: (String) -> Unit,
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
    onBikeFilterSelected: (String?) -> Unit = {},
    onAppointmentClick: (String) -> Unit = {},
    onAddAppointmentClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            if (!uiState.isLoading && uiState.upcomingAppointments.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onAddAppointmentClick
                ) {
                    Text(text = "Book service")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Appointments",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Review upcoming services and previous work for each bike.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                BikeFilterChips(
                    filters = uiState.bikeFilters,
                    selectedBikeId = uiState.selectedBikeId,
                    onBikeFilterSelected = onBikeFilterSelected
                )
            }

            item {
                when {
                    uiState.isLoading -> Text(text = "Loading appointments...")
                    else -> AppointmentsTabs(
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

private enum class AppointmentsTab(val title: String) {
    Upcoming("Upcoming"),
    Past("Past")
}

@Composable
private fun BikeFilterChips(
    filters: List<AppointmentBikeFilter>,
    selectedBikeId: String?,
    onBikeFilterSelected: (String?) -> Unit
) {
    if (filters.isEmpty()) return

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
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
                selected = selectedBikeId == filter.bikeId,
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
    upcomingAppointments: List<Appointment>,
    pastAppointments: List<Appointment>,
    selectedBikeName: String?,
    onAppointmentClick: (String) -> Unit,
    onAddAppointmentClick: () -> Unit
) {
    var currentTab by rememberSaveable { mutableStateOf(AppointmentsTab.Upcoming) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
    onAppointmentClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        appointments.forEach { appointment ->
            AppointmentCard(
                appointment = appointment,
                actionHint = actionHint,
                onClick = { onAppointmentClick(appointment.id) }
            )
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: Appointment,
    modifier: Modifier = Modifier,
    actionHint: String,
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
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = appointment.dateText,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = appointment.bikeName ?: appointment.bikeId,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            appointment.status?.let { status ->
                Text(
                    text = status.toDisplayStatus(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = actionHint,
                style = MaterialTheme.typography.bodySmall
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

private fun String.toDisplayStatus(): String {
    return lowercase()
        .split("_", "-", " ")
        .filter { it.isNotBlank() }
        .joinToString(separator = " ") { part ->
            part.replaceFirstChar { char -> char.uppercase() }
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
                .map { AppointmentBikeFilter(it.bikeId, it.bikeName ?: it.bikeId) }
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
