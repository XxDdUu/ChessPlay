package com.sky.chessplay.domain.repository

import com.sky.chessplay.domain.model.Move
import kotlinx.coroutines.flow.Flow
import model.state.GameState

interface ChessRepository {
    fun connect(gameId: String, token: String)
    fun observeGame(): Flow<GameState>
    fun sendMove(gameId: String, move: Move)
    fun disconnect()
}

