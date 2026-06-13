package com.sky.chessplay.navigation

sealed class Route(val route: String) {
    data object Home : Route("home")
    data object OfflinePlay: Route("offline-play")
    data object MultiplayerOfflinePlay: Route("multiplayer-offline-play")
    data object OnlinePlay: Route ("online-play?isTournament={isTournament}&tournamentId={tournamentId}") {
        fun createRoute(isTournament: Boolean, tournamentId: Long): String {
            return "online-play?isTournament=$isTournament&tournamentId=$tournamentId"
        }
    }
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
    data object Replay : Route("replay")
    data object Tournament : Route("tournament")
    data object TournamentDetail : Route("tournament_detail/{tournamentId}") {
        fun createRoute(tournamentId: Long): String {
            return "tournament_detail/$tournamentId"
        }
    }
    data object TournamentStandings : Route("tournament_standings/{tournamentId}") {
        fun createRoute(tournamentId: Long): String {
            return "tournament_standings/$tournamentId"
        }
    }
    data object TournamentLobby : Route("tournament_lobby/{tournamentId}") {
        fun createRoute(tournamentId: Long): String {
            return "tournament_lobby/$tournamentId"
        }
    }
    data object TournamentBreak : Route("tournament_break/{tournamentId}/{breakDurationSeconds}") {
        fun createRoute(tournamentId: Long, breakDurationSeconds: Int = 600): String {
            return "tournament_break/$tournamentId/$breakDurationSeconds"
        }
    }
    data object Admin : Route("admin")
}
