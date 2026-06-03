package com.sky.chessplay.domain.engine

import com.sky.chessplay.domain.engine.util.toPgn
import com.sky.chessplay.domain.model.chess.Move
import com.sky.chessplay.domain.model.chess.Position
import com.sky.chessplay.domain.repository.LocalMatchRepository
import com.sky.chessplay.domain.rules.CheckDetector
import com.sky.chessplay.domain.rules.MoveValidator
import com.sky.chessplay.domain.socket.GameStatus
import com.sky.chessplay.domain.socket.SocketEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import model.service.applyMove
import model.service.move.pseudoLegalMoves
import model.state.GameState
import javax.inject.Inject

class LocalChessEngine @Inject constructor(
    private val localMatchRepository: LocalMatchRepository,
) : ChessEngine {
    private val _gameState = MutableStateFlow(GameState())
    override val gameStateFlow: StateFlow<GameState> = _gameState

    override fun getLegalMoves(gameState: GameState, position: Position): List<Move> {
        val piece = gameState.piecesByPosition[position] ?: return emptyList()
        return MoveValidator.legalMoves(gameState)
    }

    override fun makeMove(gameState: GameState, move: Move): GameState {
        val newState = gameState.applyMove(move)

        val nextSide = newState.sideToPlay
        val legalMoves = getAllLegalMovesForSide(newState, nextSide)
        
        val finalState = if (legalMoves.isEmpty()) {
            val isCheck = CheckDetector.isKingInCheck(newState, nextSide)
            val result = if (isCheck) {
                if (nextSide == com.sky.chessplay.domain.model.chess.Side.WHITE) "0-1" else "1-0"
            } else {
                "1/2-1/2"
            }
            val reason = if (isCheck) "Checkmate" else "Stalemate"
            val finishedState = newState.copy(
                status = GameStatus.FINISHED,
                result = result,
                reason = reason
            )

            // Serialize move history to PGN
            val pgnMoves = mutableListOf<String>()
            var tempState = GameState()
            val moves = finishedState.boardSnapshots.mapNotNull { it.move }
            for (m in moves) {
                pgnMoves.add(m.toPgn(tempState))
                tempState = tempState.applyMove(m)
            }

            // Save match to database asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                localMatchRepository.saveMatch(result, reason, pgnMoves)
            }

            finishedState
        } else {
            newState
        }

        _gameState.value = finalState
        return finalState
    }

    override fun initNewGame() {
        _gameState.value = GameState().copy(status = GameStatus.PLAYING)
    }

    override fun loadGame(gameInit: SocketEvent.GameInit) {
    }

    private fun getAllLegalMovesForSide(gameState: GameState, side: com.sky.chessplay.domain.model.chess.Side): List<Move> {
        return gameState.piecesByPosition.entries
            .filter { it.value.side == side }
            .flatMap { (position, piece) ->
                piece.pseudoLegalMoves(gameState, true).filter { move ->
                    MoveValidator.isMoveLegal(gameState, move, side)
                }
            }
    }
}
