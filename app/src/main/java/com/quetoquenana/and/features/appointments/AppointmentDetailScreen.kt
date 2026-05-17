package com.quetoquenana.and.features.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.AppointmentService

@Composable
fun AppointmentDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: AppointmentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    AppointmentDetailScreen(
        modifier = modifier,
        uiState = uiState,
        onRetryClick = viewModel::retry
    )
}

@Composable
fun AppointmentDetailScreen(
    uiState: AppointmentDetailUiState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { paddingValues ->
        when (uiState) {
            AppointmentDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AppointmentDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = onRetryClick) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            is AppointmentDetailUiState.Content -> {
                AppointmentDetailContent(
                    appointment = uiState.appointment,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun AppointmentDetailContent(
    appointment: Appointment,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AppointmentHeaderCard(appointment = appointment)
        }

        if (appointment.requestedServices.isNotEmpty()) {
            item {
                Text(
                    text = "Services",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(appointment.requestedServices, key = { it.id }) { service ->
                ServiceItemRow(
                    service = service,
                    currency = appointment.currency
                )
            }
            item {
                val total = appointment.requestedServices
                    .mapNotNull { it.price?.toBigDecimalOrNull() }
                    .fold(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                ServiceTotalRow(
                    total = total.toPlainString(),
                    currency = appointment.currency
                )
            }
        }
    }
}

@Composable
private fun AppointmentHeaderCard(
    appointment: Appointment,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Title row
            Text(
                text = "Appointment",
                style = MaterialTheme.typography.headlineSmall
            )

            HorizontalDivider()

            // Bike
            DetailRow(
                label = "Bike",
                value = appointment.bikeName ?: appointment.bikeId
            )

            // Store location
            val locationDisplay = buildString {
                append(appointment.storeLocationName ?: appointment.storeLocationId ?: "—")
                appointment.currency?.let { append(" · $it") }
            }
            DetailRow(label = "Location", value = locationDisplay)

            // Scheduled date
            appointment.scheduledAt?.let {
                DetailRow(label = "Scheduled", value = appointment.dateText)
            }

            // Status
            appointment.status?.let {
                DetailRow(label = "Status", value = it)
            }

            // Deposit
            appointment.deposit?.let { deposit ->
                val depositDisplay = if (appointment.currency != null) {
                    "$deposit ${appointment.currency}"
                } else {
                    deposit
                }
                DetailRow(label = "Deposit", value = depositDisplay)
            }

            // Notes
            appointment.notes?.takeIf { it.isNotBlank() }?.let {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
private fun ServiceItemRow(
    service: AppointmentService,
    currency: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = service.productName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            service.price?.let { price ->
                val display = if (currency != null) "$price $currency" else price
                Text(
                    text = display,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ServiceTotalRow(
    total: String,
    currency: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Total",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        val display = if (currency != null) "$total $currency" else total
        Text(
            text = display,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
