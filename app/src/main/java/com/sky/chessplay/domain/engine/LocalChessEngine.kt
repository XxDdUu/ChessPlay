package com.sky.chessplay.domain.engine

import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.rules.MoveValidator.legalMoves
import model.service.applyMove
import model.state.GameState
import javax.inject.Inject

class LocalChessEngine @Inject constructor() : ChessEngine {
    override fun getLegalMoves(gameState: GameState, position: Position): List<Move> {
        val piece = gameState.piecesByPosition[position] ?: return emptyList()
        return legalMoves(gameState)
    }

    override fun makeMove(move: Move, gameState: GameState, callback: (GameState) -> Unit) {
        callback(gameState.applyMove(move))
    }
}
