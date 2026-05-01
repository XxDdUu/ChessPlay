package com.sky.chessplay.domain.socket

sealed class MatchEvent {

    object Searching : MatchEvent()

    data class PrepareGame(
        val gameId: String,
        val opponentId: Long,
        val opponentName: String,
        val opponentCountry: String?,
        val opponentRating: Int?,
        val timeout: Int
    ) : MatchEvent()

    data class Opponent(
        val id: Long,
        val username: String,
        val countryCode: String?,
        val rating: Int?
    )
    data class MatchCancelled(
        val reason: String
    ) : MatchEvent()

    data class GameStart(
        val gameId: String,
        val side: String,
        val fen: String,
        val opponentName: String?,
        val opponentRating: Int?
    ) : MatchEvent()

    data class ReconnectGame(
        val gameId: String,
        val side: String,
        val fen: String,
        val opponentId: Long,
        val opponentName: String,
        val opponentRating: Int?
    ) : MatchEvent()

    data class Error(
        val message: String
    ) : MatchEvent()
}
