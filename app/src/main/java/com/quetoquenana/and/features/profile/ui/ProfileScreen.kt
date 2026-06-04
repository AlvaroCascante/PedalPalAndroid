package com.quetoquenana.and.features.profile.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.toImageMediaUploadRequest
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.core.ui.navigation.Profile
import com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@Composable
fun ProfileRoute(
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val profile = (uiState.profileLoadingState as? ProfileLoadingState.Success)?.profile
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var selectedProfileId by remember { mutableStateOf<UUID?>(null) }
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val profileId = selectedProfileId ?: run {
            coroutineScope.launch {
                snackBarHostState.showSnackbar("Profile is not loaded yet")
            }
            return@rememberLauncherForActivityResult
        }

        coroutineScope.launch {
            val request = withContext(Dispatchers.IO) {
                context.toImageMediaUploadRequest(
                    referenceId = profileId,
                    uri = uri,
                    mediaType = MediaReferenceType.PROFILE,
                )
            }
            if (request != null) {
                viewModel.onProfilePhotoSelected(request)
            } else {
                snackBarHostState.showSnackbar("No valid image selected")
            }
            selectedProfileId = null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileViewModel.ProfileEvent.NavigateStartup -> onLoggedOut()
                is ProfileViewModel.ProfileEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
                is ProfileViewModel.ProfileEvent.ShowMessage -> snackBarHostState.showSnackbar(event.message)
            }
        }
    }

    ProfileScreen(
        modifier = modifier,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onLogoutClick = viewModel::onLogoutClicked,
        onEditPhotoClick = {
            val profileId = profile?.id ?: run {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar("Profile is not loaded yet")
                }
                return@ProfileScreen
            }
            selectedProfileId = profileId
            pickerLauncher.launch("image/*")
        }
    )
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileUiState = ProfileUiState(),
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    onLogoutClick: () -> Unit = {},
    onEditPhotoClick: () -> Unit = {},
) {
    val shouldShowStickyLogout = uiState.profileLoadingState is ProfileLoadingState.Success

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            if (shouldShowStickyLogout) {
                StickyBottomCta(
                    text = if (uiState.isLoggingOut) "Logging out..." else "Logout",
                    onClick = onLogoutClick,
                    enabled = !uiState.isLoggingOut
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val profileState = uiState.profileLoadingState) {
                ProfileLoadingState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ProfileLoadingState.Error -> {
                    ErrorContent(
                        message = profileState.message
                    )
                }

                is ProfileLoadingState.Success -> {
                    ProfileContent(
                        profile = profileState.profile,
                        isUploadingPhoto = uiState.isUploadingPhoto,
                        onEditPhotoClick = onEditPhotoClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: ProfileUiModel,
    isUploadingPhoto: Boolean,
    onEditPhotoClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        EditableProfileImage(
            photoUrl = profile.photoUrl,
            isUploading = isUploadingPhoto,
            onClick = onEditPhotoClick,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = listOf(
                profile.name,
                profile.lastname
            )
                .filter { it.isNotBlank() }
                .joinToString(separator = " "),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileField(
            label = "Nickname",
            value = profile.nickname
        )

        ProfileField(
            label = "Username",
            value = profile.username
        )

        ProfileField(
            label = "ID number",
            value = profile.idNumber
        )
    }
}

@Composable
private fun EditableProfileImage(
    photoUrl: String?,
    isUploading: Boolean,
    onClick: () -> Unit,
) {
    val fallbackPainter = painterResource(id = com.quetoquenana.and.R.drawable.mobi_bike_logo)

    Surface(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable(enabled = !isUploading, onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = fallbackPainter,
                    error = fallbackPainter,
                    fallback = fallbackPainter
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile image placeholder",
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit profile image",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (isUploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                }
            }
        }
    }
}


@Composable
private fun ProfileField(
    label: String,
    value: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value?.takeIf { it.isNotBlank() } ?: "—",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ProfileScreenContentPreview() {

    PedalPalTheme {
        val navController = rememberNavController()
        val currentRoute = Profile.route
        val showBottomBar =
            shouldShowBottomBar(
                currentRoute
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
            ProfileScreen(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize()
            )
        }
    }
}