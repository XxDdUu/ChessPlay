package com.sky.chessplay.ui.presentation.tournament

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.data.local.datastore.TokenManager
import com.sky.chessplay.domain.repository.TournamentRepository
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.SocketEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LobbyUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val pairing: com.sky.chessplay.domain.model.tournament.MyPairing? = null,
    val gameIdToNavigate: String? = null,
    val nextRoundNumberToNavigate: Int? = null,
    val breakDurationSeconds: Int = 600,
    val isForfeited: Boolean = false,
    val forfeitMessage: String? = null,
    val isCompleted: Boolean = false,
    val completedMessage: String? = null
)

@HiltViewModel
class TournamentLobbyViewModel @Inject constructor(
    private val tournamentRepo: TournamentRepository,
    private val socketClient: ChessSocket,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState = _uiState.asStateFlow()

    private val _timeLeft = MutableStateFlow(0L)
    val timeLeft = _timeLeft.asStateFlow()

    private var currentTournamentId: Long? = null
    private var timerJob: Job? = null
    private var socketCollectionJob: Job? = null
    private var pollingJob: Job? = null

    fun loadLobby(tournamentId: Long) {
        currentTournamentId = tournamentId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val token = tokenManager.getToken()
            if (token != null) {
                socketClient.connect(token)
            }
            fetchLobby(tournamentId)
            startPolling(tournamentId)
            observeSocketEvents()
        }
    }

    private suspend fun fetchLobby(tournamentId: Long) {
        tournamentRepo.getMyPairing(tournamentId)
            .onSuccess { pairing ->
                _uiState.update { it.copy(isLoading = false, pairing = pairing) }
                if (pairing != null) {
                    if (pairing.inBreak) {
                        timerJob?.cancel()
                        _uiState.update {
                            it.copy(
                                nextRoundNumberToNavigate = pairing.roundNumber,
                                breakDurationSeconds = pairing.breakTimeLeftSeconds.toInt()
                            )
                        }
                        return@onSuccess
                    }
                    if (pairing.result != null) {
                        timerJob?.cancel()
                        val iAmWhite = pairing.myColor == "WHITE"
                        val iWon = (pairing.result == "1-0" && iAmWhite) || (pairing.result == "0-1" && !iAmWhite)
                        val isDraw = pairing.result == "1/2-1/2"

                        if (!pairing.iAmReady && pairing.gameId == null) {
                            val msg = if (pairing.result == "0-0") {
                                "Cả hai đấu thủ đều bị xử thua do không điểm danh!"
                            } else {
                                "Bạn bị xử thua do không điểm danh đúng giờ!"
                            }
                            _uiState.update { it.copy(isForfeited = true, forfeitMessage = msg, isCompleted = false) }
                        } else {
                            val msg = if (isDraw) {
                                "Kết quả: Hòa (½ - ½)"
                            } else if (iWon) {
                                if (pairing.gameId == null) {
                                    "Bạn đã thắng cuộc do đối thủ không điểm danh! (1 - 0)"
                                } else {
                                    "Chúc mừng! Bạn đã thắng ván đấu này! (1 - 0)"
                                }
                            } else {
                                if (pairing.gameId == null) {
                                    "Bạn bị xử thua do không điểm danh đúng giờ! (0 - 1)"
                                } else {
                                    "Bạn đã thua ván đấu này. (0 - 1)"
                                }
                            }
                            _uiState.update { it.copy(isCompleted = true, completedMessage = msg, isForfeited = false) }
                        }
                    } else if (pairing.gameId != null) {
                        timerJob?.cancel()
                        _uiState.update { it.copy(gameIdToNavigate = pairing.gameId) }
                    } else {
                        startCountdown(pairing.lobbyTimeLeftSeconds)
                    }
                } else {
                    timerJob?.cancel()
                    _timeLeft.value = 0L
                }
            }
            .onFailure { err ->
                _uiState.update { it.copy(isLoading = false, error = err.message) }
            }
    }

    private fun startPolling(tournamentId: Long) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(5000L)
                fetchLobby(tournamentId)
            }
        }
    }

    private fun startCountdown(initialSeconds: Long) {
        timerJob?.cancel()
        _timeLeft.value = initialSeconds
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000L)
                _timeLeft.value -= 1
            }
        }
    }

    fun markReady() {
        val pairing = _uiState.value.pairing ?: return
        viewModelScope.launch {
            socketClient.sendTournamentJoinLobby(pairing.pairingId)
            // Instantly update local UI to show marked ready
            _uiState.update { state ->
                state.copy(
                    pairing = state.pairing?.copy(iAmReady = true)
                )
            }
        }
    }

    private fun observeSocketEvents() {
        socketCollectionJob?.cancel()
        socketCollectionJob = viewModelScope.launch {
            socketClient.socketEvents.collect { event ->
                when (event) {
                    is SocketEvent.TournamentLobbyUpdate -> {
                        val currentPairing = _uiState.value.pairing
                        if (currentPairing != null && currentPairing.pairingId == event.pairingId) {
                            _uiState.update { state ->
                                state.copy(
                                    pairing = state.pairing?.copy(
                                        iAmReady = event.whiteReady && currentPairing.myColor == "WHITE" || event.blackReady && currentPairing.myColor == "BLACK",
                                        opponentReady = event.whiteReady && currentPairing.myColor == "BLACK" || event.blackReady && currentPairing.myColor == "WHITE"
                                    )
                                )
                            }
                        }
                    }
                    is SocketEvent.TournamentMatchStart -> {
                        val currentPairing = _uiState.value.pairing
                        if (currentPairing != null && currentPairing.pairingId == event.pairingId) {
                            _uiState.update { it.copy(gameIdToNavigate = event.gameId) }
                        }
                    }
                    is SocketEvent.TournamentPairingForfeited -> {
                        val currentPairing = _uiState.value.pairing
                        if (currentPairing != null && currentPairing.pairingId == event.pairingId) {
                            _uiState.update { it.copy(isForfeited = true, forfeitMessage = "Match forfeited. Result: ${event.result}") }
                        }
                    }
                    is SocketEvent.RoundBreakStart -> {
                        _uiState.update { it.copy(nextRoundNumberToNavigate = event.nextRoundNumber, breakDurationSeconds = event.breakDurationSeconds) }
                    }
                    is SocketEvent.RoundStarted -> {
                        // Reload lobby for new round
                        _uiState.update { it.copy(
                            nextRoundNumberToNavigate = null,
                            isCompleted = false,
                            completedMessage = null,
                            isForfeited = false,
                            forfeitMessage = null
                        ) }
                        val tId = currentTournamentId
                        if (tId != null) {
                            fetchLobby(tId)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        socketCollectionJob?.cancel()
        pollingJob?.cancel()
    }
}
