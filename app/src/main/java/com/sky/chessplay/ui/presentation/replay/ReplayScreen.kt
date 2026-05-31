package com.sky.chessplay.ui.presentation.replay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.domain.engine.ChessMoveExecutor.applySinglePgnMove
import com.sky.chessplay.domain.engine.util.boardToFen
import com.sky.chessplay.domain.engine.util.parsePgnToMoves
import com.sky.chessplay.domain.model.profile.GameHistoryItem
import com.sky.chessplay.ui.component.replay.ReplayContent
import com.sky.chessplay.ui.state.UiState
import kotlinx.coroutines.delay
import model.state.GameState
import view.board.ChessBoard

@Composable
fun ReplayScreen(
    game: GameHistoryItem,
    onCloseReplay: () -> Unit
) {
    val moveList = remember(game.pgn) { parsePgnToMoves(game.pgn ?: "") }
    var currentMoveIndex by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var playSpeedMs by remember { mutableStateOf(1500L) }

    LaunchedEffect(isPlaying, currentMoveIndex, playSpeedMs) {
        if (isPlaying && currentMoveIndex < moveList.size) {
            delay(playSpeedMs)
            currentMoveIndex++
        } else if (currentMoveIndex >= moveList.size) {
            isPlaying = false
        }
    }

    val initialBoard = remember { GameState().piecesByPosition }
    var boardState by remember { mutableStateOf(initialBoard) }
    var previousIndex by remember { mutableStateOf(0) }

    LaunchedEffect(currentMoveIndex) {
        if (currentMoveIndex == 0) {
            boardState = initialBoard
        } else if (currentMoveIndex == previousIndex + 1) {
            val lastMoveStr = moveList[currentMoveIndex - 1]
            val isWhiteTurn = (currentMoveIndex - 1) % 2 == 0
            boardState = applySinglePgnMove(boardState, lastMoveStr, isWhiteTurn)
        } else {
            var tempBoard = initialBoard
            for (i in 0 until currentMoveIndex) {
                val moveStr = moveList[i]
                val isWhiteTurn = i % 2 == 0
                tempBoard = applySinglePgnMove(tempBoard, moveStr, isWhiteTurn)
            }
            boardState = tempBoard
        }
        previousIndex = currentMoveIndex
    }

    val currentGameState = remember(boardState) {
        val currentTurnSign = if (currentMoveIndex % 2 == 0) "w" else "b"
        GameState.fromFen(boardToFen(boardState, currentTurnSign))
    }

    val currentUiState = remember {
        UiState(
            squareSize = 1,
            pieceDragOffset = Offset.Zero,
            canInteract = { _ -> false },
            shouldAnimate = { _ -> true },
            isFlipped = game.myColor.equals("BLACK", ignoreCase = true)        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(0xFF1C1A17))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 0.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onCloseReplay) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
            }
            Text(
                text = "vs ${game.opponentName ?: "Unknown"} (${game.result})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            ChessBoard(
                gameState = currentGameState,
                uiState = currentUiState,
                onClick = {}, onDragStart = {}, onDrag = {}, onDragEnd = {},
                onSquareSizeChanged = {}, applyPromotion = {}, cancelPromotion = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }

        HorizontalDivider(color = Color(0xFF2D2B28), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            ReplayContent(
                moves = moveList,
                currentIndex = currentMoveIndex,
                isPlaying = isPlaying,
                currentSpeedMs = playSpeedMs,
                onIndexChange = { currentMoveIndex = it },
                onPlayPauseToggle = { isPlaying = !isPlaying },
                onSpeedChange = { playSpeedMs = it }
            )
        }
    }
}