package com.quetoquenana.and.features.profile.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.core.ui.navigation.Profile
import com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.profile.domain.model.ProfilePhotoUploadRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileRoute(
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        coroutineScope.launch {
            val request = withContext(Dispatchers.IO) {
                context.toProfilePhotoUploadRequest(uri)
            }
            if (request != null) {
                viewModel.onProfilePhotoSelected(request)
            } else {
                snackBarHostState.showSnackbar("No valid image selected")
            }
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
        onEditPhotoClick = { pickerLauncher.launch("image/*") }
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
    val shouldShowStickyLogout = !uiState.isLoading

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
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    EditableProfileImage(
                        photoUrl = uiState.profile?.photoUrl,
                        profileMediaId = uiState.profile?.profileMediaId,
                        isUploading = uiState.isUploadingPhoto,
                        onClick = onEditPhotoClick
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = listOf(
                            uiState.profile?.name.orEmpty(),
                            uiState.profile?.lastname.orEmpty()
                        )
                            .filter { it.isNotBlank() }
                            .joinToString(separator = " ")
                            .ifBlank { "Profile" },
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ProfileField(label = "Nickname", value = uiState.profile?.nickname)
                    ProfileField(label = "Username", value = uiState.profile?.username)
                    ProfileField(label = "ID number", value = uiState.profile?.idNumber)
                    ProfileField(label = "Status", value = uiState.profile?.userStatus)
                }
            }
        }
    }
}

@Composable
private fun EditableProfileImage(
    photoUrl: String?,
    profileMediaId: String?,
    isUploading: Boolean,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val imageRequest = remember(photoUrl, profileMediaId) {
        photoUrl?.takeIf { it.isNotBlank() }?.let {
            ImageRequest.Builder(context)
                .data(it)
                .memoryCacheKey(profileMediaId ?: it)
                .diskCacheKey(profileMediaId ?: it)
                .build()
        }
    }

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
            if (imageRequest != null) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
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

private fun Context.toProfilePhotoUploadRequest(uri: Uri): ProfilePhotoUploadRequest? {
    val contentType = resolveContentType(uri)
        ?.takeIf { it.startsWith(prefix = "image/") }
        ?: return null
    val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
        ?: return null
    val displayName = resolveDisplayName(uri)

    return ProfilePhotoUploadRequest(
        name = "Profile",
        altText = displayName ?: "Profile image",
        contentType = contentType,
        bytes = bytes
    )
}

private fun Context.resolveDisplayName(uri: Uri): String? {
    val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
    return contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        if (!cursor.moveToFirst()) return@use null
        val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (columnIndex < 0) return@use null
        cursor.getString(columnIndex)
    } ?: uri.lastPathSegment?.substringAfterLast('/')
}

private fun Context.resolveContentType(uri: Uri): String? {
    contentResolver.getType(uri)?.let { return it }
    val extension = resolveDisplayName(uri)
        ?.substringAfterLast('.', missingDelimiterValue = "")
        ?.lowercase()
        ?.takeIf { it.isNotBlank() }
        ?: return null
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
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