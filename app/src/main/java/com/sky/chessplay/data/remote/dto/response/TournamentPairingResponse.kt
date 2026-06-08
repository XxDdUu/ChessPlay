package com.sky.chessplay.data.remote.dto.response

data class TournamentPairingResponse(
    val pairingId: Long?,
    val whitePlayerId: Long?,
    val whitePlayerName: String?,
    val whitePlayerRating: Int?,
    val blackPlayerId: Long?,
    val blackPlayerName: String?,
    val blackPlayerRating: Int?,
    val result: String?,
    val isBye: Boolean?,
    val gameId: Long?
)
