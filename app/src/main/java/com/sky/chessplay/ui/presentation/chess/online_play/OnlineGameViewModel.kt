package com.sky.chessplay.ui.presentation.chess.online_play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.domain.model.chess.Side
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.MatchEvent
import com.sky.chessplay.domain.socket.SocketEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnlineGameViewModel @Inject constructor(
    private val chessSocket: ChessSocket
) : ViewModel() {

    var rematchOffered by mutableStateOf(false)
        private set

    var drawOffered by mutableStateOf(false)
        private set

    var drawSent by mutableStateOf(false)
        private set

    var rematchSent by mutableStateOf(false)
        private set

    var gameOverReason by mutableStateOf<String?>(null)
        private set

    var gameOverResult by mutableStateOf<String?>(null)
        private set

    var isTournament by mutableStateOf(false)
    var tournamentId by mutableStateOf<Long?>(null)

    // --- Clock State ---
    var whiteTimeSeconds by mutableIntStateOf(600)
        private set

    var blackTimeSeconds by mutableIntStateOf(600)
        private set

    /** Which side's clock is currently ticking. Null = paused (game not PLAYING). */
    private var activeSide: Side? = null

    private var clockJob: Job? = null

    init {
        observeSocket()
    }

    private fun observeSocket() {
        viewModelScope.launch {
            chessSocket.events.collect { event ->
                when (event) {
                    is MatchEvent.RematchOffered -> rematchOffered = true
                    is MatchEvent.RematchRejected -> rematchSent = false
                    is MatchEvent.GameStart -> resetGameState()
                    else -> {}
                }
            }
        }

        viewModelScope.launch {
            chessSocket.socketEvents.collect { event ->
                when (event) {
                    is SocketEvent.GameOver -> {
                        gameOverReason = event.reason
                        gameOverResult = event.result
                        stopClock()
                    }
                    is SocketEvent.GameInit -> {
                        initializeGame(event, Side.WHITE)
                    }

                    is SocketEvent.DrawOffered -> drawOffered = true

                    is SocketEvent.DrawResponse -> {
                        if (event.accepted) drawOffered = false
                        drawSent = false
                    }

                    is SocketEvent.Move -> {
                        event.fen?.let { fen ->
                            val turnPart = fen.split(" ").getOrNull(1)
                            val sideThatMoved = if (turnPart == "w") Side.BLACK else Side.WHITE
                            event.timeRemaining?.let { remaining ->
                                if (sideThatMoved == Side.WHITE) whiteTimeSeconds = remaining
                                else blackTimeSeconds = remaining
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Clock helpers
    // -------------------------------------------------------------------------

    fun initializeGame(event: SocketEvent.GameInit, startingSide: Side) {
        resetGameState()
        whiteTimeSeconds = event.timeWhite
        blackTimeSeconds = event.timeBlack
        startClock(startingSide)
    }

    fun setActiveSide(side: Side?) {
        if (side == null) {
            stopClock()
        } else {
            startClock(side)
        }
    }

    private fun startClock(side: Side) {
        stopClock()
        activeSide = side
        clockJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                if (side == Side.WHITE) {
                    whiteTimeSeconds = (whiteTimeSeconds - 1).coerceAtLeast(0)
                } else {
                    blackTimeSeconds = (blackTimeSeconds - 1).coerceAtLeast(0)
                }
            }
        }
    }

    private fun stopClock() {
        clockJob?.cancel()
        clockJob = null
        activeSide = null
    }

    /** Called when the local player successfully makes a move (flip the ticking side). */
    fun onLocalMoveMade(nextTurnSide: Side) {
        startClock(nextTurnSide)
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    fun offerRematch(gameId: String) {
        chessSocket.sendRematchOffer(gameId)
        rematchSent = true
    }

    fun offerDraw(gameId: String) {
        chessSocket.sendDrawOffer(gameId)
        drawSent = true
    }

    fun acceptDraw(gameId: String) {
        chessSocket.sendDrawResponse(gameId, true)
        drawOffered = false
    }

    fun rejectDraw(gameId: String) {
        chessSocket.sendDrawResponse(gameId, false)
        drawOffered = false
    }

    fun acceptRematch(gameId: String) {
        chessSocket.sendRematchResponse(gameId, true)
        rematchOffered = false
    }

    fun resign(gameId: String) {
        chessSocket.sendResign(gameId)
    }

    fun rejectRematch(gameId: String) {
        chessSocket.sendRematchResponse(gameId, false)
        rematchOffered = false
    }

    private fun resetGameState() {
        rematchOffered = false
        rematchSent = false
        drawOffered = false
        drawSent = false
        gameOverReason = null
        gameOverResult = null
        stopClock()
    }
}