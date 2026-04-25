package com.sky.chessplay.ui.presentation.offline_play

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import view.board.ChessBoard

@Composable
fun OfflinePlayScreen(
    viewModel: ChessViewModel = hiltViewModel(),
) {
    val gameState = viewModel.gameState
    val uiState = viewModel.uiState
    LaunchedEffect(Unit) {
        viewModel.startGame(isOnline = false)
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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
