package com.quetoquenana.and.core.ui.navigation

import android.net.Uri

sealed interface Screen {
    val route: String
    val label: String
    val showBottomBar: Boolean
    val showTopBar: Boolean
}

object AddAppointment : Screen {
    override val route: String = "appointments/add"
    override val label = "Add Appointment"
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = true
}

object AddBike : Screen {
    override val route: String = "bikes/add?name={name}&model={model}&notes={notes}"
    override val label = "Add Bike"
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = false

    fun createRoute(
        name: String? = null,
        model: String? = null,
        notes: String? = null
    ): String {
        return buildString {
            append("bikes/add")
            append("?name=${Uri.encode(name.orEmpty())}")
            append("&model=${Uri.encode(model.orEmpty())}")
            append("&notes=${Uri.encode(notes.orEmpty())}")
        }
    }
}

object StravaImport : Screen {
    override val route: String = "bikes/strava"
    override val label = "Import from Strava"
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = false
}

object Appointments : Screen {
    override val route: String = "appointments"
    override val label = "Appointments"
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true
}

object AppointmentDetail : Screen {
    override val route: String = "appointment/{id}"
    override val label = "Appointment"
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true

    fun createRoute(id: String): String = "appointment/$id"
}

object Bikes : Screen {
    override val route: String = "bikes"
    override val label = "Bikes"
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true
}

object BikeDetail : Screen {
    override val route: String = "bikes/{id}"
    override val label = "Bike"
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true

    fun createRoute(id: String): String = "bikes/${Uri.encode(id)}"
}

object BikeHistory : Screen {
    override val route: String = "bikes/{id}/history"
    override val label = "Bike History"
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true

    fun createRoute(id: String): String = "bikes/${Uri.encode(id)}/history"
}

object BikeComponentOptions : Screen {
    override val route: String = "bikes/{bikeId}/components/{componentId}/options"
    override val label = "Component"
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true

    fun createRoute(bikeId: String, componentId: String): String {
        return "bikes/${Uri.encode(bikeId)}/components/${Uri.encode(componentId)}/options"
    }
}

object CompleteProfile : Screen {
    override val route: String = "complete_profile"
    override val label = "Complete Profile"
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = false
}

object Home : Screen {
    override val route: String = "home"
    override val label = "Home"
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true
}

object Authentication : Screen {
    override val route: String = "authentication"
    override val label = "Authentication"
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = false
}

object Profile : Screen {
    override val route: String = "profile"
    override val label = "Profile"
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = false
}

object Startup : Screen {
    override val route: String = "startup"
    override val label = "startup"
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = false
}
