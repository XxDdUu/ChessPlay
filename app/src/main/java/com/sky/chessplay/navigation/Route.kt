package com.sky.chessplay.navigation

sealed class Route(val route: String) {
    data object Home : Route("home")
    data object OfflinePlay: Route("offline-play")
    data object MultiplayerOfflinePlay: Route("multiplayer-offline-play")
    data object OnlinePlay: Route ("online-play")
    data object OnlineGameMode: Route ("online-gamemode")
    data object Auth : Route("authentication")
    data object Friend : Route("community-friend")
    data object Profile : Route("profile")
    data object AIPlay : Route(
        "ai-play/{model}/{difficulty}/{side}"
    ) {

        fun createRoute(
            model: String,
            difficulty: Int,
            side: String
        ): String {

            return "ai-play/$model/$difficulty/$side"
        }
    }
}