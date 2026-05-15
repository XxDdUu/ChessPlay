package com.sky.chessplay.domain.engine

import com.sky.chessplay.domain.model.chess.Move
import com.sky.chessplay.domain.model.chess.Position
import com.sky.chessplay.domain.socket.SocketEvent
import kotlinx.coroutines.flow.StateFlow
import model.state.GameState

interface ChessEngine {

    fun initNewGame()
    fun loadGame(gameInit: SocketEvent.GameInit)
    fun getLegalMoves(gameState: GameState, position: Position): List<Move>
    fun makeMove(gameState: GameState, move: Move): GameState
    val gameStateFlow: StateFlow<GameState>
}

