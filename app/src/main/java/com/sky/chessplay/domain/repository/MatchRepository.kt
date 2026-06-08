package com.sky.chessplay.domain.repository

interface MatchRepository {

    suspend fun joinMatchmaking(userId: Long, matchType: String = "RAPID")

    suspend fun leaveMatchmaking(userId: Long)
}
