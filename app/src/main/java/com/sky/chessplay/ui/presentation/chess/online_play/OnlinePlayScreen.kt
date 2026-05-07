package com.sky.chessplay.ui.presentation.chess.online_play

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.sky.chessplay.domain.model.DEFAULT_FEN
import com.sky.chessplay.ui.component.online_play.OnlineInfoPanel
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

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(gameInit) {
        gameInit?.let { init ->

            val fen = init.fen.takeIf { it.isNotBlank() }
                ?: DEFAULT_FEN

            viewModel.startOnlineGame(
                init.copy(fen = fen)
            )
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center

    ) {

        if (isLandscape) {

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {

                Box(
                    modifier = Modifier.weight(1f),
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

                OnlineInfoPanel(
                    gameState = gameState,
                    viewModel = viewModel,
                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight()
                )
            }

        } else {

            // PORTRAIT
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OnlineInfoPanel(
                        gameState = gameState,
                        viewModel = viewModel,
                        modifier = Modifier.wrapContentWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
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
            }
        }
    }
    }
}