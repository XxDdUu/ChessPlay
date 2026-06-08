package com.sky.chessplay.domain.model.tournament

data class Standing(
    val playerId: Long,
    val username: String,
    val currentScore: Double,
    val buchholz: Double,
    val sonnebornBerger: Double,
    val rank: Int
)