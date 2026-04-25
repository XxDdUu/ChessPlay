package com.sky.chessplay.domain.engine

import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.rules.MoveValidator.legalMoves
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import model.service.applyMove
import model.state.GameState
import javax.inject.Inject

class LocalChessEngine @Inject constructor() : ChessEngine {
    private val _gameState = MutableStateFlow(GameState())
    override val gameStateFlow: StateFlow<GameState> = _gameState
    override fun getLegalMoves(gameState: GameState, position: Position): List<Move> {
        val piece = gameState.piecesByPosition[position] ?: return emptyList()
        return legalMoves(gameState)
    }
    override fun makeMove(gameState: GameState, move: Move): GameState {
        val newState = gameState.applyMove(move)

        _gameState.value = newState

        return newState
    }
}
