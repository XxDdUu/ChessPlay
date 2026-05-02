package com.sky.chessplay.ui.presentation.chess.online_play

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.ui.presentation.chess.ChessViewModel
import view.board.ChessBoard

@Composable
fun OnlinePlayScreen(
    viewModel: ChessViewModel,
    matchViewModel: MatchViewModel
) {
    val gameState = viewModel.gameState
    val uiState = viewModel.uiState
    val gameInit = matchViewModel.gameInitEvent

    LaunchedEffect(gameInit) {
        gameInit?.let {
            viewModel.startOnlineGame(it)
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Opponent: ${gameState.opponent?.name ?: "..."}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Rating: ${gameState.opponent?.rating ?: "--"}",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Side: ${gameState.mySide}",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = viewModel.buildStatusText(gameState),
                fontSize = 16.sp,
                color = if (gameState.isMyTurn) Color.Green else Color.Gray
            )
        }

        // ♟ BOARD
        Box(
            modifier = Modifier.align(Alignment.Center)
        ) {
            ChessBoard(
                gameState = gameState,
                uiState = uiState,
                onClick = viewModel::onClick,
                onDragStart = viewModel::onDragStart,
                onDrag = viewModel::onDrag,
                onDragEnd = viewModel::onDragEnd,
                applyPromotion = viewModel::applyPromotion,
                cancelPromotion = viewModel::cancelPromotion,
                onSquareSizeChanged = viewModel::onSquareSizeChanged
            )
        }
    }
}
