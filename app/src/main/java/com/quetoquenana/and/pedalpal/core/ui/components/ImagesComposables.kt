package com.quetoquenana.and.pedalpal.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.quetoquenana.and.pedalpal.R

@Composable
fun LogoImage() {
    Image(
        painter = painterResource(R.drawable.mobi_bike_logo),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .size(160.dp),
        alignment = Alignment.Center
    )
}

@Composable
fun AppointmentCard() {
    Image(
        painter = painterResource(R.drawable.mobi_bike_logo),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .size(160.dp),
        alignment = Alignment.Center
    )
}