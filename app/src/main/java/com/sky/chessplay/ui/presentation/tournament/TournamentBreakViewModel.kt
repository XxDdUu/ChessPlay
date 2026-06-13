package com.sky.chessplay.ui.presentation.tournament

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.SocketEvent
import com.sky.chessplay.domain.usecases.GetMyPairingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TournamentBreakViewModel @Inject constructor(
    private val socketClient: ChessSocket,
    private val getMyPairingUseCase: GetMyPairingUseCase
) : ViewModel() {

    private val _nextRoundStarted = MutableStateFlow(false)
    val nextRoundStarted = _nextRoundStarted.asStateFlow()

    private val _breakTimeLeft = MutableStateFlow<Long?>(null)
    val breakTimeLeft = _breakTimeLeft.asStateFlow()

    private var socketJob: Job? = null

    init {
        socketJob = viewModelScope.launch {
            socketClient.socketEvents.collect { event ->
                if (event is SocketEvent.RoundStarted) {
                    _nextRoundStarted.value = true
                }
            }
        }
    }

    fun loadBreakTime(tournamentId: Long) {
        viewModelScope.launch {
            getMyPairingUseCase(tournamentId).fold(
                onSuccess = { myPairing ->
                    if (myPairing != null && myPairing.inBreak) {
                        _breakTimeLeft.value = myPairing.breakTimeLeftSeconds
                    } else {
                        _nextRoundStarted.value = true
                    }
                },
                onFailure = {
                    // ignore
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketJob?.cancel()
    }
}
