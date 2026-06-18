package com.sky.chessplay.domain.model.profile

data class UserStats(
    val rating: Int,
    val gamesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val winRate: Double = 0.0,
    val goldMedals: Int = 0,
    val silverMedals: Int = 0,
    val bronzeMedals: Int = 0,
    val tournamentHistory: List<UserTournament> = emptyList()
)

data class UserTournament(
    val tournamentId: Int,
    val tournamentName: String,
    val startTime: String?,
    val status: String,
    val rank: Int,
    val medal: String?,
    val score: Double
)