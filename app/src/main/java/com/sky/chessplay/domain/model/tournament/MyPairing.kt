package com.sky.chessplay.domain.model.tournament

data class MyPairing(
    val pairingId: Long,
    val roundNumber: Int,
    val opponentName: String,
    val opponentRating: Int,
    val myColor: String,
    val isBye: Boolean,
    val lobbyTimeLimitSeconds: Int,
    val lobbyTimeLeftSeconds: Long,
    val iAmReady: Boolean,
    val opponentReady: Boolean,
    val result: String?,
    val gameId: String?,
    val inBreak: Boolean = false,
    val breakTimeLeftSeconds: Long = 0L
)
