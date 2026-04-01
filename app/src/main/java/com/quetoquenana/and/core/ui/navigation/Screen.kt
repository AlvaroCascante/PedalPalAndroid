package com.quetoquenana.and.core.ui.navigation

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
