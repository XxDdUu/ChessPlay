package com.sky.chessplay.navigation

sealed class Route(val route: String) {
    data object Home : Route("home")
    data object PlayOffline: Route("play-offline")
    data object PlayOnline: Route ("play-online")
}