package com.sky.chessplay.ui.presentation.online_play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sky.chessplay.data.socket.ChessSocketClient
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.socket.SocketEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import model.state.GameState
import model.state.fromFen
import javax.inject.Inject

@HiltViewModel
class OnlineChessViewModel @Inject constructor(
    private val socketClient: ChessSocketClient
) : ViewModel() {

    var gameState by mutableStateOf(GameState())
        private set

    init {
        socketClient.connect("123", "YOUR_JWT")

        socketClient.observeEvents { event ->
            handleEvent(event)
        }
    }

    private fun handleMessage(event: SocketEvent) {
        when (event) {

            is SocketEvent.Connected -> {
                // optional: show UI connected
            }

            is SocketEvent.GameStart -> {
                // init game
            }

            is SocketEvent.GameUpdate -> {
                gameState = gameState.fromFen(event.fen)
            }

            is SocketEvent.Reconnect -> {
                gameState = gameState.fromFen(event.fen)
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
        socketClient.sendMove("123", move)
    }

    fun ready() {
        socketClient.sendReady("123")
    }
    private fun handleEvent(event: SocketEvent) {
        when (event) {

            // 🔌 CONNECTED
            is SocketEvent.Connected -> {
                println("✅ Connected to server")
                // có thể gửi READY ngay nếu muốn auto start
                // socketClient.sendReady(currentGameId)
            }

            // 🔌 DISCONNECTED
            is SocketEvent.Disconnected -> {
                println("❌ Disconnected from server")
                // TODO: show reconnect UI
            }

            // ♟️ GAME START
            is SocketEvent.GameStart -> {
                println("🎮 Game started: ${event.gameId}, side=${event.side}")

                // reset board về initial FEN (server đã set)
                gameState = gameState.fromFen(
                    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
                )

                // TODO: lưu side của player (WHITE / BLACK)
            }

            // ♟️ OPPONENT MOVE / UPDATE
            is SocketEvent.GameUpdate -> {
                println("♟️ Opponent move: ${event.move}")

                // update board từ FEN server gửi
                gameState = gameState.fromFen(event.fen)
            }

            // 🔄 RECONNECT
            is SocketEvent.Reconnect -> {
                println("🔄 Reconnected to game ${event.gameId}")

                gameState = gameState.fromFen(event.fen)

                // TODO:
                // - restore history nếu cần
                // - restore side
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
        }
    }
}
