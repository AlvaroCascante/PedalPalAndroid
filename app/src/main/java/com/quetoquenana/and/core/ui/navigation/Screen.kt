package com.quetoquenana.and.core.ui.navigation

import android.net.Uri
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.quetoquenana.and.R
import com.quetoquenana.and.core.utils.NAV_ARG_BRAND
import com.quetoquenana.and.core.utils.NAV_ARG_EXTERNAL_GEAR_ID
import com.quetoquenana.and.core.utils.NAV_ARG_MODEL
import com.quetoquenana.and.core.utils.NAV_ARG_NAME
import com.quetoquenana.and.core.utils.NAV_ARG_NOTES
import com.quetoquenana.and.core.utils.NAV_ARG_ODOMETER
import java.util.UUID

sealed interface Screen {
    val route: String
    val label: Int
    val showBottomBar: Boolean
    val showTopBar: Boolean
}

object AddAppointment : Screen {
    override val route: String = "appointments/add"
    override val label = R.string.add_appointment
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = true
}

object AddBike : Screen {
    override val route: String = "bikes/add?name={name}&model={model}&notes={notes}&odometerKm={odometerKm}&externalGearId={externalGearId}"
    override val label = R.string.add_bike
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = true

    val arguments = listOf(
        navArgument(NAV_ARG_NAME) {
            type = NavType.StringType
            defaultValue = ""
            nullable = true
        },
        navArgument(NAV_ARG_BRAND) {
            type = NavType.StringType
            defaultValue = ""
            nullable = true
        },
        navArgument(NAV_ARG_MODEL) {
            type = NavType.StringType
            defaultValue = ""
            nullable = true
        },
        navArgument(NAV_ARG_NOTES) {
            type = NavType.StringType
            defaultValue = ""
            nullable = true
        },
        navArgument(NAV_ARG_ODOMETER) {
            type = NavType.StringType
            defaultValue = ""
            nullable = true
        },
        navArgument(NAV_ARG_EXTERNAL_GEAR_ID) {
            type = NavType.StringType
            defaultValue = ""
            nullable = true
        })

    fun createRoute(
        name: String? = null,
        brand: String? = null,
        model: String? = null,
        notes: String? = null,
        odometerKm: String? = null,
        externalGearId: String? = null
    ): String {
        return buildString {
            append("bikes/add")
            append("?").append(NAV_ARG_NAME).append("=${Uri.encode(name.orEmpty())}")
            append("&").append(NAV_ARG_BRAND).append("=${Uri.encode(brand.orEmpty())}")
            append("&").append(NAV_ARG_MODEL).append("=${Uri.encode(model.orEmpty())}")
            append("&").append(NAV_ARG_NOTES).append("=${Uri.encode(notes.orEmpty())}")
            append("&").append(NAV_ARG_ODOMETER).append("=${Uri.encode(odometerKm.orEmpty())}")
            append("&").append(NAV_ARG_EXTERNAL_GEAR_ID).append("=${Uri.encode(externalGearId.orEmpty())}")
        }
    }
}

object StravaImport : Screen {
    // expose an optional boolean query param `fromDeepLink` so callers can indicate
    // the screen was opened via an external deep link and the screen can react accordingly.
    override val route: String = "bikes/strava?fromDeepLink={fromDeepLink}"
    override val label = R.string.import_from_strava
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = false

    fun createRoute(fromDeepLink: Boolean = false): String {
        return "bikes/strava?fromDeepLink=$fromDeepLink"
    }
}

object Appointments : Screen {
    override val route: String = "appointments"
    override val label = R.string.appointments
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true
}

object AppointmentDetail : Screen {
    override val route: String = "appointment/{id}"
    override val label = R.string.appointment
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = true

    fun createRoute(id: UUID): String = "appointment/$id"
}

object Bikes : Screen {
    override val route: String = "bikes"
    override val label = R.string.bikes
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true
}

object BikeDetail : Screen {
    override val route: String = "bikes/{id}"
    override val label = R.string.bike
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true

    fun createRoute(id: UUID): String = "bikes/$id"
}

object BikeHistory : Screen {
    override val route: String = "bikes/{id}/history"
    override val label = R.string.bike_history
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true

    fun createRoute(id: UUID): String = "bikes/$id/history"
}

object BikeImages : Screen {
    override val route: String = "bikes/{id}/images"
    override val label = R.string.bike_images
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true

    fun createRoute(id: UUID): String = "bikes/$id/images"
}

object BikeComponent : Screen {
    override val route: String = "bikes/{bikeId}/components/{componentId}/options"
    override val label = R.string.component
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true

    fun createRoute(bikeId: UUID, componentId: UUID?): String {
        return "bikes/$bikeId/components/$componentId/options"
    }
}

object CompleteProfile : Screen {
    override val route: String = "complete_profile"
    override val label = R.string.complete_profile
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = false
}

object Home : Screen {
    override val route: String = "home"
    override val label = R.string.home
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = true
}

object Authentication : Screen {
    override val route: String = "authentication"
    override val label = R.string.authentication
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = false
}

object Profile : Screen {
    override val route: String = "profile"
    override val label = R.string.profile
    override val showBottomBar: Boolean = true
    override val showTopBar: Boolean = false
}

object Startup : Screen {
    override val route: String = "startup"
    override val label = R.string.startup
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = false
}
