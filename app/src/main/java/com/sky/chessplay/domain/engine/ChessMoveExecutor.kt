package com.sky.chessplay.domain.engine

import com.sky.chessplay.domain.engine.util.findMoveFromPgn
import com.sky.chessplay.domain.model.chess.Board

object ChessMoveExecutor {
    fun applySinglePgnMove(
        currentBoard: Board,
        pgnMove: String,
        isWhiteTurn: Boolean
    ): Board {
        val moveEffect =
            findMoveFromPgn(
                currentBoard,
                pgnMove,
                isWhiteTurn
            )

        return moveEffect?.applyOn(currentBoard)
            ?: currentBoard
    }
}