package com.sky.chessplay.domain.engine.util

import com.sky.chessplay.domain.model.chess.Board
import model.state.GameState

fun getFenAtMove(moveList: List<String>, index: Int): String {
    val initialGameState = GameState()
    var currentBoard: Board = initialGameState.piecesByPosition
    var isWhiteTurn = true

    if (index <= 0 || moveList.isEmpty()) {
        return boardToFen(currentBoard, "w")
    }

    val currentMoves = moveList.take(index)

    for (moveStr in currentMoves) {
        val moveEffect = findMoveFromPgn(currentBoard, moveStr, isWhiteTurn)
        if (moveEffect != null) {
            currentBoard = moveEffect.applyOn(currentBoard)
        }
        isWhiteTurn = !isWhiteTurn
    }

    return boardToFen(currentBoard, if (isWhiteTurn) "w" else "b")
}