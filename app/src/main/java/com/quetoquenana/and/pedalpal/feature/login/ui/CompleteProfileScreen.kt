package com.quetoquenana.and.pedalpal.feature.login.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Simple profile completion screen that collects nickname and person fields.
 */
@Composable
fun CompleteProfileScreen(onComplete: () -> Unit) {
    val nickname = remember { mutableStateOf("") }
    val idNumber = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val lastname = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        androidx.compose.material3.OutlinedTextField(
            value = nickname.value,
            onValueChange = { nickname.value = it },
            label = { Text("Nickname") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.OutlinedTextField(
            value = idNumber.value,
            onValueChange = { idNumber.value = it },
            label = { Text("ID Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("First name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.OutlinedTextField(
            value = lastname.value,
            onValueChange = { lastname.value = it },
            label = { Text("Last name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onComplete() }) {
            Text("Complete profile")
        }
    }
}
