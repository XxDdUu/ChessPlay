package com.sky.chessplay.data.engine

import android.util.Log
import com.sky.chessplay.domain.engine.ChessEngine
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.rules.MoveValidator.legalMoves
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.SocketEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import model.state.GameState

class  RemoteChessEngine(
    private val socket: ChessSocket
) : ChessEngine {
    private var gameId: String? = null
    private val _gameState = MutableStateFlow(GameState())
    override val gameStateFlow: StateFlow<GameState> = _gameState
    val current = _gameState.value

    init {
        socket.observeEvents { event ->
            when (event) {

                is SocketEvent.GameInit -> {
                    gameId = event.gameId
                    loadGame(event)
                }


                is SocketEvent.Move -> {
                    val current = _gameState.value
                    Log.d("SOCKET_MOVE", "move=${event.move}, fen=${event.fen}")
                    event.fen?.let {
                        _gameState.value = GameState.fromFen(it).copy(
                            opponent = current.opponent,
                            mySide = current.mySide,
                            status = current.status
                        )
                    }
                }


                else -> Unit
            }
        }
    }
    override fun initNewGame() {}

    override fun getLegalMoves(gameState: GameState, position: Position): List<Move> {
        return legalMoves(gameState)
    }
    override fun loadGame(gameInit: SocketEvent.GameInit) {
        gameId = gameInit.gameId

        _gameState.value = GameState.fromFen(gameInit.fen).copy(
            opponent = SocketEvent.Opponent(
                name = gameInit.opponentName ?: "Unknown",
                rating = gameInit.opponentRating ?: 1200
            ),
            mySide = gameInit.side,
            status = SocketEvent.GameStatus.PLAYING
        )
    }

    override fun makeMove(gameState: GameState, move: Move): GameState {
        socket.sendMove(move, gameId)
        return gameState
    }
}
