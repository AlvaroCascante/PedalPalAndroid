package com.quetoquenana.and.features.appointments.domain.model

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val closedAppointmentStatuses = setOf(
    "COMPLETED",
    "CANCELLED",
    "CANCELED",
    "NO_SHOW",
    "CLOSED"
)

fun Appointment.isUpcoming(currentTimeMillis: Long = System.currentTimeMillis()): Boolean {
    val scheduledTimeMillis = scheduledAtMillis ?: return false
    return !isClosed && scheduledTimeMillis >= currentTimeMillis
}

val Appointment.isClosed: Boolean
    get() = status.orEmpty().uppercase() in closedAppointmentStatuses

val Appointment.scheduledAtMillis: Long?
    get() = scheduledAt?.let { value ->
        parseUtcMillis(value)
    }

private fun parseUtcMillis(value: String): Long? {
    return listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ssX"
    ).firstNotNullOfOrNull { pattern ->
        runCatching {
            SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
                isLenient = false
            }.parse(value)?.time
        }.getOrNull()
    }
}


