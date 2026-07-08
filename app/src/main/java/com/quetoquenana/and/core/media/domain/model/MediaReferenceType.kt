package com.quetoquenana.and.core.media.domain.model

enum class MediaReferenceType(val mediaName: String) {
    ANNOUNCEMENT(mediaName = "announcement"),
    APPOINTMENT_DEPOSIT(mediaName = "appointment-deposit"),
    APPOINTMENT_PAYMENT(mediaName = "appointment-payment"),
    BIKE(mediaName = "bike"),
    BIKE_PROFILE(mediaName = "bike-profile"),
    COMPONENT(mediaName = "component"),
    PROFILE(mediaName = "profile"),
    SERVICE_ORDER(mediaName = "service-order"),
    OTHER(mediaName = "other");
}

