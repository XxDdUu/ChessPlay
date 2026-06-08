package com.sky.chessplay.data.repository

import com.sky.chessplay.data.remote.api.MatchApi
import com.sky.chessplay.domain.repository.MatchRepository
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val api: MatchApi
) : MatchRepository {

    override suspend fun joinMatchmaking(userId: Long, matchType: String) {
        api.joinMatch(userId, matchType)
    }

    override suspend fun leaveMatchmaking(userId: Long) {
        api.leaveMatch(userId)
    }
}
