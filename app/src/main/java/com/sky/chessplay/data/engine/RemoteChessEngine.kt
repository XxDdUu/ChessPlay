package com.sky.chessplay.data.engine

import com.sky.chessplay.domain.engine.ChessEngine
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.rules.MoveValidator.legalMoves
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.SocketEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import model.state.GameState

class RemoteChessEngine(
    private val socket: ChessSocket
) : ChessEngine {

    private val _gameState = MutableStateFlow(GameState())
    override val gameStateFlow: StateFlow<GameState> = _gameState

    init {
        socket.observeEvents { event ->
            when (event) {

                is SocketEvent.GameUpdate -> {
                    val newState = GameState.fromFen(event.fen)
                    _gameState.value = newState
                }

                is SocketEvent.GameStart -> {
                    val newState = GameState.fromFen(event.fen)
                    _gameState.value = newState
                }

                is SocketEvent.Reconnect -> {
                    val newState = GameState.fromFen(event.fen)
                    _gameState.value = newState
                }

                else -> Unit
            }
        }
    }

    override fun getLegalMoves(gameState: GameState, position: Position): List<Move> {
        return legalMoves(gameState)
    }

    override fun makeMove(gameState: GameState, move: Move): GameState {
        socket.sendMove(move)
        return gameState
    }
}
