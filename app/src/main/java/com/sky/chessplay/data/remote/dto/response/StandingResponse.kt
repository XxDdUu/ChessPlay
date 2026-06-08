package com.sky.chessplay.data.remote.dto.response

data class StandingResponse (
    val playerId: Long?,
    val userId: Long?,
    val username: String?,
    val initialRating: Int?,
    val currentScore: Double?,
    val buchholz: Double?,
    val sonnebornBerger: Double?,
    val rank: Int?
)
