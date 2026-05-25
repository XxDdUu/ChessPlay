package com.sky.chessplay.ui.presentation.chess.online_play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.MatchEvent
import com.sky.chessplay.domain.socket.SocketEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnlineGameViewModel @Inject constructor(
    private val chessSocket: ChessSocket
) : ViewModel() {

    var rematchOffered by mutableStateOf(false)
        private set

    var rematchSent by mutableStateOf(false)
        private set

    var gameOverReason by mutableStateOf<String?>(null)
        private set

    var gameOverResult by mutableStateOf<String?>(null)
        private set

    init {
        observeSocket()
    }

    private fun observeSocket() {
        viewModelScope.launch {
            chessSocket.events.collect { event ->
                when(event) {
                    is MatchEvent.RematchOffered -> {
                        rematchOffered = true
                    }
                    is MatchEvent.RematchRejected -> {
                        rematchSent = false
                    }
                    is MatchEvent.GameStart -> {
                        rematchOffered = false
                        rematchSent = false
                        gameOverReason = null
                        gameOverResult = null
                    }
                    else -> {}
                }
            }
        }

        viewModelScope.launch {
            chessSocket.socketEvents.collect { event ->
                when(event) {
                    is SocketEvent.GameOver -> {
                        gameOverReason = event.reason
                        gameOverResult = event.result
                    }
                    is SocketEvent.GameInit -> {
                        rematchOffered = false
                        rematchSent = false
                        gameOverReason = null
                        gameOverResult = null
                    }
                    else -> {}
                }
            }
        }
    }

    fun offerRematch(gameId: String) {
        chessSocket.sendRematchOffer(gameId)
        rematchSent = true
    }

    fun acceptRematch(gameId: String) {
        chessSocket.sendRematchResponse(
            gameId,
            true
        )

        rematchOffered = false
    }

    fun rejectRematch(gameId: String) {
        chessSocket.sendRematchResponse(
            gameId,
            false
        )

        rematchOffered = false
    }
}