package com.sky.chessplay.domain.rules

import com.sky.chessplay.domain.model.Capture
import com.sky.chessplay.domain.model.Side
import model.board.King
import model.service.move.pseudoLegalMoves
import model.state.GameState

object CheckDetector {

    fun isKingInCheck(gameState: GameState, kingSet: Side): Boolean {
        val kingPosition = gameState.piecesByPosition
            .entries
            .firstOrNull { (_, piece) ->
                piece is King && piece.side == kingSet
            }?.key ?: return false

        val opponentPieces = gameState.piecesByPosition
            .values
            .filter { it.side != kingSet }

        return opponentPieces.any { piece ->
            piece.pseudoLegalMoves(gameState)
                .filterIsInstance<Capture>()
                .any { it.to == kingPosition }
        }
    }
}
