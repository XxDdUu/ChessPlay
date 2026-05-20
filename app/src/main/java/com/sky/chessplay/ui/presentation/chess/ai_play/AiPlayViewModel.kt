package com.sky.chessplay.ui.presentation.chess.ai_play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.data.engine.AiChessEngine
import com.sky.chessplay.data.remote.dto.response.AiModelInfo
import com.sky.chessplay.di.AiEngine
import com.sky.chessplay.domain.engine.ChessEngine
import com.sky.chessplay.domain.model.chess.Position
import com.sky.chessplay.domain.model.chess.Promotion
import com.sky.chessplay.domain.model.chess.Side
import com.sky.chessplay.domain.repository.AiRepository
import com.sky.chessplay.domain.socket.SocketEvent
import com.sky.chessplay.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import model.service.ChessUiService
import model.state.GameState
import javax.inject.Inject

@HiltViewModel
class AiPlayViewModel @Inject constructor(
    private val aiRepository: AiRepository,
    private val uiService: ChessUiService,
    @AiEngine private val aiEngine: ChessEngine
) : ViewModel() {

    private val aiChessEngine = aiEngine as AiChessEngine

    var gameState by mutableStateOf(uiService.gameState)
        private set

    var uiState by mutableStateOf(uiService.uiState)
        private set

    var isThinking by mutableStateOf(false)
        private set

    var apiError by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Configuration Settings
    var aiModels by mutableStateOf<List<AiModelInfo>>(emptyList())
        private set
    var selectedModel by mutableStateOf("best_model")
    var difficulty by mutableStateOf(3) // Default to 3
    var playerColor by mutableStateOf(Side.WHITE)

    var gameId by mutableStateOf<String?>(null)
        private set

    var gameHistory by mutableStateOf<List<String>>(emptyList())
        private set

    init {
        uiService.updateOnStateChanges {
            gameState = it
            uiState = uiService.uiState
        }

        // Collect engine state flows
        viewModelScope.launch {
            aiChessEngine.isThinking.collect { thinking ->
                isThinking = thinking
                if (!thinking) {
                    refreshHistoryAndStatus()
                }
            }
        }

        viewModelScope.launch {
            aiChessEngine.errorMessage.collect { error ->
                apiError = error
            }
        }

        loadModels()
        checkActiveGame()
    }

    private fun loadModels() {
        viewModelScope.launch {
            try {
                val response = aiRepository.getAvailableModels()
                aiModels = response.models ?: emptyList()
                selectedModel = response.default ?: response.models?.firstOrNull()?.key ?: "best_model"
            } catch (e: Exception) {
                aiModels = listOf(AiModelInfo("best_model", "Default Best Model"))
                selectedModel = "best_model"
            }
        }
    }

    private fun checkActiveGame() {
        viewModelScope.launch {
            isLoading = true
            try {
                val active = aiRepository.checkActiveGame()
                if (active != null) {
                    gameId = active.gameId
                    selectedModel = active.aiModel
                    difficulty = active.difficulty
                    playerColor = if (active.playerColor.uppercase() == "WHITE") Side.WHITE else Side.BLACK
                    gameHistory = active.history ?: emptyList()

                    val status = if (active.isGameOver) SocketEvent.GameStatus.FINISHED else SocketEvent.GameStatus.PLAYING

                    val gameInit = SocketEvent.GameInit(
                        gameId = active.gameId,
                        side = playerColor,
                        fen = active.fen,
                        opponentId = 0,
                        opponentName = active.aiModel,
                        opponentRating = 1500 + active.difficulty * 100,
                        history = active.history
                    )
                    uiService.initAi(gameInit)
                }
            } catch (e: Exception) {
                // Ignore initial active game check failures silently
            } finally {
                isLoading = false
            }
        }
    }

    fun startGame() {
        viewModelScope.launch {
            isLoading = true
            apiError = null
            try {
                val colorStr = playerColor.name
                val response = aiRepository.startGame(selectedModel, difficulty, colorStr)
                gameId = response.gameId
                gameHistory = response.history ?: emptyList()

                val gameInit = SocketEvent.GameInit(
                    gameId = response.gameId,
                    side = playerColor,
                    fen = response.fen,
                    opponentId = 0,
                    opponentName = response.aiModel,
                    opponentRating = 1500 + response.difficulty * 100,
                    history = response.history
                )
                uiService.initAi(gameInit)
            } catch (e: Exception) {
                apiError = e.message ?: "Failed to start match"
            } finally {
                isLoading = false
            }
        }
    }

    fun resignGame() {
        val activeId = gameId ?: return
        viewModelScope.launch {
            isLoading = true
            apiError = null
            try {
                val response = aiRepository.resignGame(activeId)
                gameId = null
                gameHistory = response.history ?: emptyList()
                uiService.initLocal() // Reset/clear the board UI
            } catch (e: Exception) {
                apiError = e.message ?: "Failed to resign"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetError() {
        apiError = null
    }

    fun clearActiveGameLocally() {
        gameId = null
        gameHistory = emptyList()
        uiService.initLocal()
    }

    private suspend fun refreshHistoryAndStatus() {
        val activeId = gameId ?: return
        try {
            val response = aiRepository.getGameDetails(activeId)
            gameHistory = response.history ?: emptyList()
        } catch (e: Exception) {
            // Ignore background refresh errors
        }
    }

    // Board Interactions Delegation
    fun onClick(position: Position) = uiService.onClick(position)
    fun onDragStart(position: Position) = uiService.onDragStart(position)
    fun onDrag(offset: Offset) = uiService.onDrag(offset)
    fun onDragEnd() = uiService.onDragEnd()
    fun applyPromotion(promotion: Promotion) = uiService.applyPromotion(promotion)
    fun cancelPromotion() = uiService.cancelPromotion()
    fun onSquareSizeChanged(size: Int) = uiService.onSquareSizeChanged(size)

    fun getStatusText(): String {
        if (gameState.status == SocketEvent.GameStatus.FINISHED) {
            return "GAME OVER"
        }
        return if (isThinking) {
            "AI IS THINKING..."
        } else {
            if (gameState.isMyTurn) "YOUR TURN" else "AI TURN"
        }
    }
}
