package com.quetoquenana.and.features.appointments.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.toImageMediaUploadRequests
import com.quetoquenana.and.core.ui.components.previewAppointmentAttachments
import com.quetoquenana.and.core.ui.components.previewAppointmentDetail
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.model.AppointmentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

@Composable
fun AppointmentDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: AppointmentDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult

        coroutineScope.launch {
            val uploads = withContext(Dispatchers.IO) {
                context.toImageMediaUploadRequests(
                    referenceId = viewModel.appointmentId,
                    uris = uris,
                    mediaType = MediaReferenceType.APPOINTMENT_DEPOSIT
                )
            }
            viewModel.onPaymentProofsSelected(uploads)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is AppointmentDetailEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
                is AppointmentDetailEvent.ShowMessage -> snackBarHostState.showSnackbar(event.message)
            }
        }
    }

    AppointmentDetailScreen(
        modifier = modifier,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onRetryClick = viewModel::retry,
        onAttachPaymentClick = { pickerLauncher.launch("image/*") },
    )
}

@Composable
fun AppointmentDetailScreen(
    uiState: AppointmentDetailUiState,
    onRetryClick: () -> Unit,
    onAttachPaymentClick: () -> Unit,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) { paddingValues ->
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
                    attachments = uiState.attachments,
                    isUploadingAttachment = uiState.isUploadingAttachment,
                    onAttachPaymentClick = onAttachPaymentClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun AppointmentDetailContent(
    appointment: Appointment,
    attachments: List<MediaAsset>,
    isUploadingAttachment: Boolean,
    onAttachPaymentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AppointmentHeaderCard(
                appointment = appointment,
                attachments = attachments,
                isUploadingAttachment = isUploadingAttachment,
                onAttachPaymentClick = onAttachPaymentClick,
            )
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
                    .fold(BigDecimal.ZERO, BigDecimal::add)
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
    attachments: List<MediaAsset>,
    isUploadingAttachment: Boolean,
    onAttachPaymentClick: () -> Unit,
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
                value = appointment.bikeName
            )

            // Store location
            val locationDisplay = buildString {
                append(
                    appointment.storeLocationName
                        ?.takeUnless { it.isBlank() || it == appointment.storeLocationId.toString() }
                        ?: "Location unavailable"
                )
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
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DetailRow(label = "Deposit", value = depositDisplay)

                    if (deposit.isZeroAmount()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "To confirm the appointment, please make and report an initial payment of 5,000 CRC. That amount will be credited toward the final total.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Click here to attach payment",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable(onClick = onAttachPaymentClick)
                            )
                        }
                    }
                }
            }

            if (attachments.isNotEmpty() || isUploadingAttachment) {
                AppointmentAttachmentsSection(
                    attachments = attachments,
                    isUploading = isUploadingAttachment,
                    onAddAnotherClick = onAttachPaymentClick,
                )
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
private fun AppointmentAttachmentsSection(
    attachments: List<MediaAsset>,
    isUploading: Boolean,
    onAddAnotherClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Payment attachments",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (isUploading) "Uploading..." else "Add another",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(enabled = !isUploading, onClick = onAddAnotherClick),
            )
        }

        if (attachments.isEmpty()) {
            Text(
                text = "Uploading selected payment proof...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            attachments
                .filter { it.contentType.startsWith(prefix = "IMAGE_") || it.contentType.startsWith(prefix = "image/") }
                .forEach { attachment ->
                    AppointmentAttachmentCard(media = attachment)
                }
        }
    }
}

@Composable
private fun AppointmentAttachmentCard(
    media: MediaAsset,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imageRequest = remember(media.mediaId, media.url) {
        ImageRequest.Builder(context)
            .data(media.url)
            .memoryCacheKey(media.mediaId.toString())
            .diskCacheKey(media.mediaId.toString())
            .build()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AsyncImage(
                model = imageRequest,
                contentDescription = media.altText ?: media.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.7f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = media.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                media.altText?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (media.isPrivate) {
                    Text(
                        text = "Private attachment",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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

private fun String.isZeroAmount(): Boolean {
    return toBigDecimalOrNull()?.compareTo(BigDecimal.ZERO) == 0
}

@Preview(showSystemUi = true)
@Composable
private fun AppointmentDetailContentPreview() {
    PedalPalTheme {
        AppointmentDetailContent(
            modifier = Modifier.fillMaxSize(),
            appointment = previewAppointmentDetail,
            attachments = previewAppointmentAttachments,
            isUploadingAttachment = false,
            onAttachPaymentClick = {},
        )
    }
}

