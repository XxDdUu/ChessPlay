package com.sky.chessplay.data.repository

import com.sky.chessplay.data.socket.ChessSocketClient
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.repository.ChessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import model.state.GameState

class ChessRepositoryImpl(
    private val socket: ChessSocketClient
) : ChessRepository {

    private val _gameState = MutableSharedFlow<GameState>()

    override fun connect(gameId: String, token: String) {
        socket.connect(gameId, token = token)
    }

    override fun observeGame(): Flow<GameState> = _gameState

    override fun sendMove(gameId: String,move: Move) {
        socket.sendMove(gameId, move)
    }

    override fun disconnect() {
        socket.disconnect()
    }
}

