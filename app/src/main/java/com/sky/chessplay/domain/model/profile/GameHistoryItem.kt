package com.sky.chessplay.domain.model.profile

data class GameHistoryItem(
    val gameId: String,
    val opponentName: String?,
    val myColor: String,
    val result: String,
    val pgn: String?,
    val playedAt: String
)