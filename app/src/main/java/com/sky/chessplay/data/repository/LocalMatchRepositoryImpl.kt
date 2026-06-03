package com.sky.chessplay.data.repository

import com.sky.chessplay.data.local.db.LocalMatchDao
import com.sky.chessplay.data.local.entity.LocalMatchEntity
import com.sky.chessplay.domain.repository.LocalMatchRepository
import javax.inject.Inject

class LocalMatchRepositoryImpl @Inject constructor(
    private val localMatchDao: LocalMatchDao
) : LocalMatchRepository {

    override suspend fun saveMatch(result: String, reason: String, moves: List<String>) {
        val moveHistoryString = moves.joinToString(" ")
        val match = LocalMatchEntity(
            result = result,
            reason = reason,
            playedAt = System.currentTimeMillis(),
            moveHistory = moveHistoryString
        )
        localMatchDao.insertMatch(match)
    }

    override suspend fun getLocalMatches(): List<LocalMatchEntity> {
        return localMatchDao.getAllMatches()
    }
}
