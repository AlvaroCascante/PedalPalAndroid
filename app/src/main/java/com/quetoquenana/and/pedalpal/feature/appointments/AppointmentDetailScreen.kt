package com.quetoquenana.and.pedalpal.feature.appointments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppointmentDetailScreen(
    appointmentId: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(24.dp)) {
        Text(text = "Appointment details")
        Text(text = "ID: $appointmentId")
    }
}
