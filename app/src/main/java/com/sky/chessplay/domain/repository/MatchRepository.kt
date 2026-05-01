package com.sky.chessplay.domain.repository

interface MatchRepository {

    suspend fun joinMatchmaking(userId: Long)

    suspend fun leaveMatchmaking(userId: Long)
}
