package com.quetoquenana.and.features.appointments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddAppointmentScreen(
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {}
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(24.dp)) {
        Text(text = "Add Appointment (placeholder)")
        Button(onClick = onDone) {
            Text("Done")
        }
    }
}
