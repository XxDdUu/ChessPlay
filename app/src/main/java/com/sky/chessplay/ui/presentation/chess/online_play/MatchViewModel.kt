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
        viewModelScope.launch {
            socketClient.socketEvents.collect { event ->

                handleSocketEvent(event)
            }
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

    var roomCode by mutableStateOf<String?>(null)
        private set
    var joinRoomCode by mutableStateOf("")
        private set

    private var confirmJob: Job? = null
    private var gameCountdownJob: Job? = null
    private var lastUserId: Long? = null
    private var currentMatchType: String = "RAPID"

    var incomingInvite by mutableStateOf<MatchEvent.MatchInvite?>(null)
        private set
    fun onNavigated() {
        navigateToGame = false
    }

    fun cancelSearch() {
        confirmJob?.cancel()
        gameCountdownJob?.cancel()

        socketClient.disconnect()
        roomCode = null
        opponent = null
        status = ""
        confirmCountdown = 10
        countdown = 5

        matchState = MatchState.INITIALIZING
    }
    fun start(userId: Long, matchType: String = "RAPID") {
        lastUserId = userId
        currentMatchType = matchType
        matchState = MatchState.SEARCHING
        status = "Checking for active games..."
        viewModelScope.launch {
            delay(1500)
            val token = tokenManager.getToken() ?: return@launch

            socketClient.connect(token)

            repo.joinMatchmaking(userId, matchType)
        }
    }
    fun updateJoinRoomCode(code: String) {
        joinRoomCode = code.uppercase()
    }
    fun joinRoom() {

        if (joinRoomCode.isBlank()) return

        status = "Joining room..."

        viewModelScope.launch {

            val token = tokenManager.getToken() ?: return@launch

            socketClient.connect(token)

            delay(500)

            socketClient.joinRoom(joinRoomCode)
        }
    }

    fun createRoom(matchType: String = "RAPID") {

        matchState = MatchState.SEARCHING
        status = "Creating room..."

        viewModelScope.launch {

            val token = tokenManager.getToken() ?: return@launch

            socketClient.connect(token)

            delay(500)

            socketClient.createRoom(matchType)
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
                repo.joinMatchmaking(it, currentMatchType)
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
            is SocketEvent.RoomCreated -> {
                roomCode = event.code
                status = "Waiting for opponent..."
                matchState = MatchState.SEARCHING
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
            MatchEvent.Searching -> Unit
            is MatchEvent.RematchAccepted -> Unit
            is MatchEvent.RematchOffered -> Unit
            MatchEvent.RematchRejected -> Unit
            is MatchEvent.MatchInvite -> {
                incomingInvite = event
            }
        }
    }
    fun acceptInvite(hostId: Long) {

        socketClient.acceptInvite(hostId)

        clearInvite()
    }
    fun clearInvite() {
        incomingInvite = null
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
    fun resetMatchState() {
        matchState = MatchState.INITIALIZING
        opponent = null
        confirmCountdown = 0
        countdown = 0
        status = ""
    }
}
