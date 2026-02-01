package com.quetoquenana.and.pedalpal.core.ui.navigation

import android.net.Uri

sealed interface Screen {
    val route: String
    val label: String
    val showBottomBar: Boolean
}

object Login : Screen {
    override val route: String = "login"
    override val label = "Login"
    override val showBottomBar: Boolean = false
}

object Home : Screen {
    override val route: String = "home"
    override val label = "Home"
    override val showBottomBar: Boolean = true
}

object CompleteProfile : Screen {
    override val route: String = "complete_profile/{name}"
    override val label = "Complete"
    override val showBottomBar: Boolean = false

    fun createRoute(name: String): String = "complete_profile/${Uri.encode(name)}"
}

object Bikes : Screen {
    override val route: String = "bikes"
    override val label = "Bikes"
    override val showBottomBar: Boolean = true
}

object Appointments : Screen {
    override val route: String = "appointments"
    override val label = "Appointments"
    override val showBottomBar: Boolean = true
}

object Profile : Screen {
    override val route: String = "profile"
    override val label = "Profile"
    override val showBottomBar: Boolean = true
}
// future: object Loyalty : Screen { override val route = "loyalty" }
