package com.sky.chessplay.domain.model.tournament

data class TournamentPairing(
    val id: Long,
    val whitePlayerId: Long,
    val whitePlayerName: String,
    val whitePlayerRating: Int,
    val blackPlayerId: Long?,
    val blackPlayerName: String,
    val blackPlayerRating: Int?,
    val result: String,
    val isBye: Boolean,
    val gameId: Long?
)
