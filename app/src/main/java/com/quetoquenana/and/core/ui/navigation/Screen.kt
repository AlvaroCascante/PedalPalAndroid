package com.quetoquenana.and.core.ui.navigation

import android.net.Uri
import com.quetoquenana.and.R
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

    fun createRoute(
        name: String? = null,
        model: String? = null,
        notes: String? = null,
        odometerKm: String? = null,
        externalGearId: String? = null
    ): String {
        return buildString {
            append("bikes/add")
            append("?name=${Uri.encode(name.orEmpty())}")
            append("&model=${Uri.encode(model.orEmpty())}")
            append("&notes=${Uri.encode(notes.orEmpty())}")
            append("&odometerKm=${Uri.encode(odometerKm.orEmpty())}")
            append("&externalGearId=${Uri.encode(externalGearId.orEmpty())}")
        }
    }
}

object StravaImport : Screen {
    override val route: String = "bikes/strava"
    override val label = R.string.import_from_strava
    override val showBottomBar: Boolean = false
    override val showTopBar: Boolean = false
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
