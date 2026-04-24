package com.sky.chessplay.domain.engine

import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.Position
import kotlinx.coroutines.flow.StateFlow
import model.state.GameState

interface ChessEngine {
    fun getLegalMoves(gameState: GameState, position: Position): List<Move>
    fun makeMove(gameState: GameState, move: Move): GameState
    val gameStateFlow: StateFlow<GameState>
}

