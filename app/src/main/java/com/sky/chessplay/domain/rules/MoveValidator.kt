package com.sky.chessplay.domain.rules

import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.Side
import model.service.applyMove
import model.state.GameState

object MoveValidator {

    fun legalMoves(gameState: GameState): List<Move> {
        val currentSide = gameState.sideToPlay

        return gameState.legalMoves.filter { move ->
            isMoveLegal(gameState, move, currentSide)
        }
    }

    fun isMoveLegal(
        gameState: GameState,
        move: Move,
        movingSet: Side
    ): Boolean {

        val newState = gameState.applyMove(move)

        return !CheckDetector.isKingInCheck(newState, movingSet)
    }
}
