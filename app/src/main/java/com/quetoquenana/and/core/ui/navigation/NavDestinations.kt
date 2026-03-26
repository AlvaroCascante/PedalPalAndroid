package com.quetoquenana.and.core.ui.navigation

val allScreens = listOf(
    Authentication,
    Home,
    CompleteProfile,
    Bikes,
    Appointments,
    Profile
)

val topBarScreens = listOf(
    Home
)

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
    val matched = allScreens.firstOrNull { routeMatches(currentRoute, it.route) }
    return matched?.showBottomBar ?: false
}

fun shouldShowTopBar(currentRoute: String?): Boolean {
    val matched = allScreens.firstOrNull { routeMatches(currentRoute, it.route) }
    return matched?.showTopBar ?: false
}
