package com.sky.chessplay.data.remote.dto.response

data class MyPairingResponse(
    val pairingId: Long?,
    val roundNumber: Int?,
    val opponentName: String?,
    val opponentRating: Int?,
    val myColor: String?,
    val isBye: Boolean?,
    val lobbyTimeLimitSeconds: Int?,
    val lobbyTimeLeftSeconds: Long?,
    val iAmReady: Boolean?,
    val opponentReady: Boolean?,
    val result: String?,
    val gameId: String?,
    val inBreak: Boolean?,
    val breakTimeLeftSeconds: Long?
)
