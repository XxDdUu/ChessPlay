package com.sky.chessplay.data.remote.dto.response

data class AdminDashboardResponse(
    val totalUsers: Long,
    val totalGames: Long,
    val totalTournaments: Long,
    val onlinePlayersCount: Long,
    val topPlayers: List<UserProfileResponse>
)
