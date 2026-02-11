package com.quetoquenana.and.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.compose.runtime.remember

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No Navigator provided") }

@Composable
fun ProvideNavigator(navController: NavHostController, content: @Composable () -> Unit) {
    val navigator = remember(key1 = navController) { NavControllerNavigator(navController) }
    CompositionLocalProvider(value = LocalNavigator provides navigator, content = content)
}
