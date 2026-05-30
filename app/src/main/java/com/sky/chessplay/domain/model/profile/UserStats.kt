package com.sky.chessplay.domain.model.profile

data class UserStats(
    val rating: Int,
    val gamesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int
)