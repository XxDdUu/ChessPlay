package com.sky.chessplay.data.engine

import com.sky.chessplay.domain.engine.ChessEngine
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.rules.MoveValidator.legalMoves
import com.sky.chessplay.domain.socket.ChessSocket
import model.state.GameState
import model.state.fromFen

class RemoteChessEngine(
    private val socket: ChessSocket
) : ChessEngine {

    override fun getLegalMoves(gameState: GameState, position: Position): List<Move> {
        // chỉ để UI highlight (optional)
        val piece = gameState.piecesByPosition[position] ?: return emptyList()
        return legalMoves(piece, gameState)
    }

    override fun makeMove(move: Move, gameState: GameState, callback: (GameState) -> Unit) {
        socket.sendMove(move)

        socket.onMoveReceived { fen ->
            val newState = gameState.fromFen(fen)
            callback(newState)
        }
    }
}
