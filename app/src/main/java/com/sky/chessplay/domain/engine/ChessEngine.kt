package com.sky.chessplay.domain.engine

import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.Position
import model.state.GameState

interface ChessEngine {
    fun getLegalMoves(gameState: GameState, position: Position): List<Move>
    fun makeMove(move: Move)

    fun observeState(listener: (GameState) -> Unit)
}

