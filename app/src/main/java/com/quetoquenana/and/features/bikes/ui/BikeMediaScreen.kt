package com.quetoquenana.and.features.bikes.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.quetoquenana.and.R
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.toImageMediaUploadRequests
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.features.bikes.domain.model.BikeMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BikeMediaRoute(
    modifier: Modifier = Modifier,
    viewModel: BikeMediaViewModel = hiltViewModel()
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
                    referenceId = viewModel.bikeId,
                    uris = uris,
                    mediaType = MediaReferenceType.BIKE
                )
            }
            viewModel.uploadMedia(uploads)
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.loadMedia()
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is BikeMediaViewModel.BikeMediaEvent.ShowError -> {
                    snackBarHostState.showSnackbar(event.message)
                }

                is BikeMediaViewModel.BikeMediaEvent.ShowMessage -> {
                    snackBarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    BikeMediaScreen(
        modifier = modifier,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onRetryClick = viewModel::loadMedia,
        onAddImagesClick = { pickerLauncher.launch("image/*") }
    )
}

@Composable
fun BikeMediaScreen(
    modifier: Modifier = Modifier,
    uiState: BikeMediaUiState,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    onRetryClick: () -> Unit = {},
    onAddImagesClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            StickyBottomCta(
                text = if (uiState.isUploading) "Uploading..." else "Add images",
                onClick = onAddImagesClick,
                enabled = !uiState.isUploading
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            when {
                uiState.isLoading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(text = "Loading images...")
                }

                uiState.isUploading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(text = "Uploading selected images...")
                }

                uiState.errorMessage != null -> item(span = { GridItemSpan(maxLineSpan) }) {
                    BikeMediaErrorCard(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick
                    )
                }

                uiState.media.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) {
                    BikeMediaEmptyCard()
                }

                else -> items(uiState.media, key = { it.id }) { media ->
                    BikeMediaCard(media = media)
                }
            }
        }
    }
}


@Composable
private fun BikeMediaCard(
    media: BikeMedia,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fallbackPainter = painterResource(id = R.drawable.mobi_bike_logo)
    val request = ImageRequest.Builder(context)
        .data(media.url)
        .memoryCacheKey(media.id.toString())
        .diskCacheKey(media.id.toString())
        .build()

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = request,
                    contentDescription = media.altText?.takeIf { it.isNotBlank() }
                        ?: media.name.takeIf { it.isNotBlank() }
                        ?: "Bike image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = fallbackPainter,
                    error = fallbackPainter,
                    fallback = fallbackPainter
                )
            }

            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                media.name.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                media.altText?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun BikeMediaEmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "No images yet", style = MaterialTheme.typography.titleMedium)
            Text(text = "This bike does not have any images available right now.")
        }
    }
}

@Composable
private fun BikeMediaErrorCard(
    message: String,
    onRetryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Unable to load images", style = MaterialTheme.typography.titleMedium)
            Text(text = message)
            Button(onClick = onRetryClick) {
                Text(text = "Retry")
            }
        }
    }
}
