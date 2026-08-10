package com.example.radiofm.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.radiofm.player.RadioPlayerManager
import com.example.radiofm.ui.screens.list.RadioListScreen
import com.example.radiofm.ui.screens.player.PlayerScreen
import com.example.radiofm.ui.theme.AppThemeMode

@Composable
fun RadioFmNavGraph(
    radioPlayerManager: RadioPlayerManager,
    themeMode: AppThemeMode,
    onToggleTheme: () -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.RadioList.route,
    ) {
        composable(
            route = Screen.RadioList.route,
            exitTransition = { fadeOut(tween(150)) },
            popEnterTransition = { fadeIn(tween(150)) },
        ) {
            RadioListScreen(
                onStationClick = { station ->
                    navController.navigate(Screen.Player.routeFor(station.id))
                },
                themeMode = themeMode,
                onToggleTheme = onToggleTheme,
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument(Screen.Player.ARG_STATION_ID) { type = NavType.StringType }),
            enterTransition = { slideInHorizontally(tween(250)) { it } + fadeIn(tween(250)) },
            popExitTransition = { slideOutHorizontally(tween(200)) { it } + fadeOut(tween(200)) },
        ) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getString(Screen.Player.ARG_STATION_ID).orEmpty()
            PlayerScreen(
                stationId = stationId,
                radioPlayerManager = radioPlayerManager,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
