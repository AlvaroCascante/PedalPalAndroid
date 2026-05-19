package com.quetoquenana.and.core.media

enum class MediaReferenceType(
    val isPublic: Boolean
) {
    ANNOUNCEMENT(true),
    APPOINTMENT(false),
    BIKE(false),
    COMPONENT(true),
    PROFILE(true),
    SERVICE_ORDER(true),
    OTHER(true);

    val apiValue: String
        get() = name
}

