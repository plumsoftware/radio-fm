package ru.plumsoftware.radiofm.navigation

sealed class Screen(val route: String) {
    data object RadioList : Screen("radio_list")

    data object Player : Screen("player/{stationId}") {
        const val ARG_STATION_ID = "stationId"
        fun routeFor(stationId: String) = "player/$stationId"
    }
}
