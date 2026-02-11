package com.quetoquenana.and.core.ui.navigation

val bottomBarScreens = listOf(
    Home,
    Bikes,
    Appointments,
    Profile
)

fun routeMatches(currentRoute: String?, screenRoute: String): Boolean {
    if (currentRoute == null) return false
    // exact match or parameterized routes (e.g., "appointments/123") -> startsWith
    return currentRoute == screenRoute || currentRoute.startsWith("$screenRoute/")
}

fun shouldShowBottomBar(currentRoute: String?): Boolean {
    // Check known screens first (including those not in bottomBarScreens e.g., Login)
    val allScreens = listOf(Login, Home, CompleteProfile, Bikes, Appointments, Profile)
    val matched = allScreens.firstOrNull { routeMatches(currentRoute, it.route) }
    return matched?.showBottomBar ?: false
}
