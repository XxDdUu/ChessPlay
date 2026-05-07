package com.sky.chessplay.ui.presentation.chess.online_play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.data.local.datastore.TokenManager
import com.sky.chessplay.domain.repository.MatchRepository
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.MatchEvent
import com.sky.chessplay.domain.socket.MatchEvent.Opponent
import com.sky.chessplay.domain.socket.SocketEvent
import com.sky.chessplay.domain.state.MatchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject


@HiltViewModel
class MatchViewModel @Inject constructor(
    private val socketClient: ChessSocket,
    private val tokenManager: TokenManager,
    private val repo: MatchRepository
) : ViewModel() {
    init {
        // Match events
        viewModelScope.launch {
            socketClient.events.collect { event ->
                handleEvent(event)
            }
        }

        // Game events
        socketClient.observeEvents { event ->
            handleSocketEvent(event)
        }
    }



    var navigateToGame by mutableStateOf(false)
        private set

    var gameInitEvent by mutableStateOf<SocketEvent.GameInit?>(null)
        private set

    var matchState by mutableStateOf(MatchState.INITIALIZING)
        private set

    var status by mutableStateOf("Initializing...")
        private set

    var countdown by mutableStateOf(5)
        private set

    var confirmCountdown by mutableStateOf(10)
        private set
    var opponent by mutableStateOf<Opponent?>(null)
        private set
    var gameId: String? = null

    private var confirmJob: Job? = null
    private var gameCountdownJob: Job? = null

    private var authToken: String? = null
    private var lastUserId: Long? = null
    fun onNavigated() {
        navigateToGame = false
    }

    fun cancelSearch() {
        confirmJob?.cancel()
        gameCountdownJob?.cancel()

        socketClient.disconnect()

        opponent = null
        status = ""
        confirmCountdown = 10
        countdown = 5

        matchState = MatchState.INITIALIZING
    }
    fun start(userId: Long) {
        lastUserId = userId
        matchState = MatchState.SEARCHING
        status = "Checking for active games..."
        viewModelScope.launch {
            delay(1500)
            val token = tokenManager.getToken() ?: return@launch

            socketClient.connect(token)

            repo.joinMatchmaking(userId)
        }
    }
    fun acceptMatch() {
        confirmJob?.cancel()
        matchState = MatchState.COUNTDOWN
        status = "Waiting opponent..."

        socketClient.sendReady(gameId)
    }
    fun rejectMatch() {
        confirmJob?.cancel()
        gameCountdownJob?.cancel()
        matchState = MatchState.CANCELLED
        status = "Match rejected"

        socketClient.disconnect()

        viewModelScope.launch {
            delay(1000)
            lastUserId?.let {
                repo.joinMatchmaking(it)
                matchState = MatchState.SEARCHING
            }
        }
    }
    private fun handleSocketEvent(event: SocketEvent) {
        when (event) {

            is SocketEvent.GameInit -> {
                gameInitEvent = event
                navigateToGame = true
            }

            else -> Unit
        }
    }

    private fun handleEvent(event: MatchEvent) {
        when (event) {

            is MatchEvent.GameStart -> {
                confirmJob?.cancel()

                opponent = opponent?.copy(
                    username = event.opponentName ?: opponent?.username ?: "Opponent",
                    rating = event.opponentRating ?: opponent?.rating ?: 1200
                )

                gameId = event.gameId

                matchState = MatchState.COUNTDOWN

                startGameCountdown()
            }

            is MatchEvent.PrepareGame -> {
                opponent = Opponent(
                    id = event.opponentId,
                    username = event.opponentName,
                    countryCode = event.opponentCountry,
                    rating = event.opponentRating
                )

                gameId = event.gameId
                confirmCountdown = event.timeout

                matchState = MatchState.FOUND
                startConfirmCountdown()
            }

            is MatchEvent.MatchCancelled -> {
                matchState = MatchState.CANCELLED
                status = event.reason
            }


            is MatchEvent.Error -> {
                matchState = MatchState.CANCELLED
                status = event.message
            }

            is MatchEvent.ReconnectGame -> {
                confirmJob?.cancel()
                gameCountdownJob?.cancel()

                opponent = Opponent(
                    id = event.opponentId,
                    username = event.opponentName,
                    countryCode = null,
                    rating = event.opponentRating
                )

                gameId = event.gameId

                matchState = MatchState.COUNTDOWN

                startGameCountdown()
            }
            MatchEvent.Searching -> TODO()
        }
    }
    private fun startConfirmCountdown() {
        confirmJob?.cancel()

        confirmJob = viewModelScope.launch {
            for (i in confirmCountdown downTo 0) {
                confirmCountdown = i
                delay(1000)
            }
            if (matchState == MatchState.FOUND) {
                rejectMatch()
            }
        }
    }
    private fun startGameCountdown() {
        gameCountdownJob?.cancel()

        gameCountdownJob = viewModelScope.launch {
            for (i in 5 downTo 0) {
                countdown = i
                delay(1000)
            }
        }
    }
}
fun handleMessage(raw: String, emit: (MatchEvent) -> Unit) {
    try {
        val msg = JSONObject(raw)
        val type = msg.getString("type")

        when (type) {

            "SEARCHING" -> {
                emit(MatchEvent.Searching)
            }

            "PREPARE_GAME" -> {
                emit(
                    MatchEvent.PrepareGame(
                        gameId = msg.getString("gameId"),
                        opponentId = msg.getLong("opponentId"),
                        opponentName = msg.getString("opponentName"),
                        opponentCountry = msg.optString("opponentCountry"),
                        opponentRating = msg.optInt("opponentRating"),
                        timeout = msg.optInt("timeout", 10)
                    )
                )
            }

            "MATCH_CANCELLED" -> {
                emit(
                    MatchEvent.MatchCancelled(
                        reason = msg.optString("reason", "Cancelled")
                    )
                )
            }

            "GAME_START" -> {
                emit(
                    MatchEvent.GameStart(
                        gameId = msg.getString("gameId"),
                        side = msg.getString("side"),
                        fen = msg.getString("fen"),
                        opponentName = msg.optString("opponentName"),
                        opponentRating = msg.optInt("opponentRating")
                    )
                )
            }

            "RECONNECT_GAME" -> {
                emit(
                    MatchEvent.ReconnectGame(
                        gameId = msg.getString("gameId"),
                        side = msg.getString("side"),
                        fen = msg.getString("fen"),
                        opponentId = msg.getLong("opponentId"),
                        opponentName = msg.getString("opponentName"),
                        opponentRating = msg.optInt("opponentRating")
                    )
                )
            }

            "ERROR" -> {
                emit(
                    MatchEvent.Error(
                        message = msg.optString("message", "Unknown error")
                    )
                )
            }
        }

    } catch (e: Exception) {
        emit(MatchEvent.Error("Parse error: ${e.message}"))
    }
}
