package com.sky.chessplay.ui.presentation.online_play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sky.chessplay.domain.engine.EngineFactory
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.SocketEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import model.state.GameState
import javax.inject.Inject

@HiltViewModel
class OnlineChessViewModel @Inject constructor(
    private val socket: ChessSocket,
    private val factory: EngineFactory
) : ViewModel() {

    var gameState by mutableStateOf(GameState())
        private set
    val engine = factory.create(isOnline = true)
    init {
        socket.connect("123")

        socket.observeEvents { event ->
            handleEvent(event)
        }
    }

    private fun handleMessage(event: SocketEvent) {
        when (event) {

            is SocketEvent.Connected -> {
                // optional: show UI connected
            }

            is SocketEvent.GameOver -> {
                // show result
            }

            is SocketEvent.Error -> {
                // show error
            }

            else -> {}
        }
    }

    fun sendMove(move: Move) {
        socket.sendMove(move)
    }

    fun ready() {
        socket.sendReady()
    }
    private fun handleEvent(event: SocketEvent) {
        when (event) {

            is SocketEvent.Connected -> {
                println("✅ Connected to server")
            }

            // 🔌 DISCONNECTED
            is SocketEvent.Disconnected -> {
                println("❌ Disconnected from server")
            }


            // 🏁 GAME OVER
            is SocketEvent.GameOver -> {
                println("🏁 Game over: ${event.result}, reason=${event.reason}")

                // TODO: show dialog kết thúc game
            }

            // 🤝 DRAW OFFER
            is SocketEvent.DrawOffered -> {
                println("🤝 Opponent offered draw")

                // TODO: show dialog accept / reject
            }

            is SocketEvent.DrawRejected -> {
                println("❌ Draw rejected")
            }

            // ❗ ERROR
            is SocketEvent.Error -> {
                println("⚠️ Error: ${event.message}")

                // TODO: show toast / snackbar
            }

            is SocketEvent.Move -> TODO()
            is SocketEvent.GameInit -> TODO()
        }
    }
}
