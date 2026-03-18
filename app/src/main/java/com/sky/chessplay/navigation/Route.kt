package com.sky.chessplay.navigation

sealed class Route(val route: String) {
    data object Home : Route("home")
    data object OfflinePlay: Route("offline-play")
    data object MultiplayerOfflinePlay: Route("multiplayer-offline-play")
    data object OnlinePlay: Route ("online-play")
}