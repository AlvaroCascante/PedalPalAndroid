package com.quetoquenana.and.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.quetoquenana.and.R

val DefaultStickyButtonHeight = 64.dp

// Default padding values for shared components
val defaultPaddingValues = PaddingValues(
    start = 16.dp,
    top = 18.dp,
    end = 16.dp,
    bottom = 18.dp
)

val defaultContainerPaddingValues = PaddingValues(
    start = 0.dp,
    top = 18.dp,
    end = 0.dp,
    bottom = 18.dp
)

val defaultFormPaddingValues = PaddingValues(
    start = 12.dp,
    top = 18.dp,
    end = 12.dp,
    bottom = 18.dp
)

// Shapes for shared components
val sharedSectionTopShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp
)

val sharedSectionTopShapeM = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp
)

val sharedCardShape = RoundedCornerShape(size = 16.dp)

val HomeAnnouncementShape = RoundedCornerShape(size = 12.dp)

// Default progress indicator
@Composable
fun DefaultProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.onPrimary
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
    }
}

// Images
@Composable
fun LogoImage(
    modifier: Modifier = Modifier,
    imageId: Int = R.drawable.mobi_bike_logo
) {
    Image(
        painter = painterResource(id = imageId),
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .size(160.dp),
        alignment = Alignment.Center
    )
}

@Composable
fun DefaultOutlinedTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.primary,             // Input text color when focused
        focusedLabelColor = MaterialTheme.colorScheme.onSecondary,      // Label when focused
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSecondary, // Placeholder when focused

        unfocusedTextColor = MaterialTheme.colorScheme.primary,     // Input text color when unfocused
        unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,    // Label when unfocused

        focusedBorderColor = MaterialTheme.colorScheme.secondary,     // Border when focused
        unfocusedBorderColor = MaterialTheme.colorScheme.primary,   // Border when unfocused

        errorBorderColor = MaterialTheme.colorScheme.errorContainer,    // Border when error
        errorLabelColor = MaterialTheme.colorScheme.error,              // Label when error
        errorTextColor = MaterialTheme.colorScheme.error,             // Input text color when error
        errorCursorColor = MaterialTheme.colorScheme.errorContainer,    // Cursor color when error
        errorSupportingTextColor = MaterialTheme.colorScheme.error,     // Supporting text when error
    )
) {

    OutlinedTextField(
        value = text,
        onValueChange = onTextChanged,
        enabled = enabled,
        isError = isError,
        label = label,
        supportingText = supportingText,
        colors = colors,
        minLines = minLines,
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        readOnly = readOnly,
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation
    )
}

//Buttons
@Composable
fun StickyBottomCta(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        colors = colors,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

//Previews
@DarkLightPreviews
@Composable
private fun DefaultProgressIndicatorPreview() {
    BasePreviewContainer {
        DefaultProgressIndicator()
    }
}

@DarkLightPreviews
@Composable
private fun StickyBottomCtaPreview() {
    BasePreviewContainer {
        StickyBottomCta(
            text = "Sticky Bottom CTA",
            onClick = {}
        )
    }
}

@DarkLightPreviews
@Composable
private fun StickyBottomCtaPreviewDisabled() {
    BasePreviewContainer {
        StickyBottomCta(
            text = "Sticky Bottom CTA",
            enabled = false,
            onClick = {}
        )
    }
}

@DarkLightPreviews
@Composable
private fun DefaultOutlinedTextFieldPreview() {
    BasePreviewContainer {
        DefaultOutlinedTextField(
            text = "Sticky Bottom CTA",
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Name") },
            onTextChanged = {}
        )
    }
}

@DarkLightPreviews
@Composable
private fun DefaultOutlinedTextFieldPreviewPlaceholder() {
    BasePreviewContainer {
        DefaultOutlinedTextField(
            text = "",
            placeholder = { Text(text = "Placeholder text") },
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Label text") },
            onTextChanged = {}
        )
    }
}

@DarkLightPreviews
@Composable
private fun DefaultOutlinedTextFieldPreviewError() {
    BasePreviewContainer {
        DefaultOutlinedTextField(
            text = "Sticky Bottom CTA",
            enabled = true,
            isError = true,
            supportingText = { Text(text = "Error message") },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Name") },
            onTextChanged = {}
        )
    }
}

@DarkLightPreviews
@Composable
private fun DefaultOutlinedTextFieldPreviewDisabled() {
    BasePreviewContainer {
        DefaultOutlinedTextField(
            text = "Sticky Bottom CTA",
            enabled = false,
            isError = false,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Name") },
            onTextChanged = {}
        )
    }
}