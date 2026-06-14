package com.sky.chessplay.ui.presentation.chess.ai_play

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.model.chess.Position
import com.sky.chessplay.domain.model.chess.Promotion
import com.sky.chessplay.ui.component.ai.MoveHistoryCard
import com.sky.chessplay.ui.component.ai.MoveHistoryDialog
import com.sky.chessplay.ui.component.ai.ResignSection
import com.sky.chessplay.ui.component.ai.StatusCard
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import com.sky.chessplay.ui.layout.TopBarAction
import com.sky.chessplay.ui.state.UiState
import model.state.GameState
import view.board.ChessBoard

@Composable
fun AiPlayScreen(

    gameState: GameState,
    uiState: UiState,

    navController: NavHostController,

    isThinking: Boolean,
    isLoading: Boolean,
    apiError: String?,

    gameId: String?,

    statusText: String,
    history: List<String>,

    onResignGame: () -> Unit,
    onDismissError: () -> Unit,

    onClick: (Position) -> Unit,
    onDragStart: (Position) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,

    onApplyPromotion: (Promotion) -> Unit,
    onCancelPromotion: () -> Unit,

    onSquareSizeChanged: (Int) -> Unit
) {

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var showHistory by remember { mutableStateOf(false) }

    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            title = "AI PLAY",
            showTopBar = true,
            showBottomBar = false,
            actions = listOf(
                TopBarAction.History(onClick = { showHistory = true })
            )
        )
    ) { padding ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1A17))
    ) {

        if (isLandscape) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // LEFT SIDE
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    StatusCard(
                        text = statusText,
                        isThinking = isThinking
                    )

                    // MoveHistoryCard removed from inline layout: now accessible via top-bar history icon
                }

                // BOARD
                Box(
                    modifier = Modifier.weight(1.2f),
                    contentAlignment = Alignment.Center
                ) {

                    ChessBoard(
                        gameState = gameState,
                        uiState = uiState,
                        onClick = onClick,
                        onDragStart = onDragStart,
                        onDrag = onDrag,
                        onDragEnd = onDragEnd,
                        applyPromotion = onApplyPromotion,
                        cancelPromotion = onCancelPromotion,
                        onSquareSizeChanged = onSquareSizeChanged
                    )
                }
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                StatusCard(
                    text = statusText,
                    isThinking = isThinking
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    ChessBoard(
                        gameState = gameState,
                        uiState = uiState,
                        onClick = onClick,
                        onDragStart = onDragStart,
                        onDrag = onDrag,
                        onDragEnd = onDragEnd,
                        applyPromotion = onApplyPromotion,
                        cancelPromotion = onCancelPromotion,
                        onSquareSizeChanged = onSquareSizeChanged
                    )
                }
                ResignSection(
                    onResignGame = onResignGame
                )
                // MoveHistoryCard removed from inline layout: use top-bar history icon to open dialog
            }
        }

        if (apiError != null) {

            AlertDialog(
                onDismissRequest = onDismissError,
                title = {
                    Text("Error")
                },
                text = {
                    Text(apiError)
                },
                confirmButton = {
                    TextButton(onClick = onDismissError) {
                        Text("OK")
                    }
                }
            )
        }

        if (showHistory) {
            MoveHistoryDialog(history = history, onDismiss = { showHistory = false })
        }
    }
    }
}