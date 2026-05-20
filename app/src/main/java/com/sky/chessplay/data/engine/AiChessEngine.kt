package com.sky.chessplay.data.engine

import com.sky.chessplay.domain.engine.ChessEngine
import com.sky.chessplay.domain.model.chess.Move
import com.sky.chessplay.domain.model.chess.Position
import com.sky.chessplay.domain.model.chess.Promotion
import com.sky.chessplay.domain.model.chess.toUci
import com.sky.chessplay.domain.repository.AiRepository
import com.sky.chessplay.domain.rules.MoveValidator.legalMoves
import com.sky.chessplay.domain.socket.SocketEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import model.service.applyMove
import model.state.GameState
import javax.inject.Inject

class AiChessEngine @Inject constructor(
    private val aiRepository: AiRepository
) : ChessEngine {
    private var gameId: String? = null
    private val _gameState = MutableStateFlow(GameState())
    override val gameStateFlow: StateFlow<GameState> = _gameState

    var isThinking: MutableStateFlow<Boolean> = MutableStateFlow(false)
        private set

    var errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
        private set

    override fun initNewGame() {
        _gameState.value = GameState()
        errorMessage.value = null
        isThinking.value = false
    }

    override fun getLegalMoves(gameState: GameState, position: Position): List<Move> {
        return legalMoves(gameState)
    }

    override fun loadGame(gameInit: SocketEvent.GameInit) {
        gameId = gameInit.gameId
        errorMessage.value = null
        isThinking.value = false

        _gameState.value = GameState.fromFen(gameInit.fen).copy(
            opponent = SocketEvent.Opponent(
                name = gameInit.opponentName ?: "AI",
                rating = gameInit.opponentRating ?: 1500
            ),
            mySide = gameInit.side,
            status = SocketEvent.GameStatus.PLAYING
        )
    }

    override fun makeMove(gameState: GameState, move: Move): GameState {
        val newState = gameState.applyMove(move)
        _gameState.value = newState

        val activeGameId = gameId
        if (activeGameId == null) {
            errorMessage.value = "No active game session"
            return newState
        }

        val promoStr = if (move is Promotion) {
            move.pieceAfterPromotion.textSymbol.lowercase()
        } else ""
        val uciMove = move.toUci() + promoStr

        isThinking.value = true
        errorMessage.value = null

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = aiRepository.makeMove(activeGameId, uciMove)
                val status = if (response.isGameOver) {
                    SocketEvent.GameStatus.FINISHED
                } else {
                    SocketEvent.GameStatus.PLAYING
                }

                _gameState.value = GameState.fromFen(response.fen).copy(
                    opponent = gameState.opponent,
                    mySide = gameState.mySide,
                    status = status
                )
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Move submission failed"
            } finally {
                isThinking.value = false
            }
        }

        return newState
    }
}
