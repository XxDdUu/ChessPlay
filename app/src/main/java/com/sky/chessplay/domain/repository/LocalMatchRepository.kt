package com.sky.chessplay.domain.repository

import com.sky.chessplay.data.local.entity.LocalMatchEntity

interface LocalMatchRepository {
    suspend fun saveMatch(result: String, reason: String, moves: List<String>)
    suspend fun getLocalMatches(): List<LocalMatchEntity>
}
