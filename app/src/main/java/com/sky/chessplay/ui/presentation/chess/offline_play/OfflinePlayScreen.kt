package com.sky.chessplay.ui.presentation.chess.offline_play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.model.chess.Side
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import com.sky.chessplay.ui.presentation.chess.ChessViewModel
import view.board.ChessBoard
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.style.TextAlign
import com.sky.chessplay.domain.socket.GameStatus
import androidx.compose.material3.OutlinedButton


@Composable
fun OfflinePlayScreen(
    navController: NavHostController,
    viewModel: ChessViewModel = hiltViewModel(),
) {
    val gameState = viewModel.gameState
    val uiState = viewModel.uiState

    // Keep track of board orientation locally so local players can flip it
    var isBoardFlipped by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.startLocalGame()
    }

    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            title = "Local Match",
            showTopBar = true,
            showBottomBar = false
        )
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F0E17) // Sleek, modern dark background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Player: BLACK (or WHITE if board is flipped)
                val topSide = if (isBoardFlipped) Side.WHITE else Side.BLACK
                val topIsActive = gameState.sideToPlay == topSide

                PlayerInfoCard(
                    playerName = if (topSide == Side.BLACK) "Player 2 (Black)" else "Player 1 (White)",
                    side = topSide,
                    isActive = topIsActive,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ChessBoard in the center
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ChessBoard(
                        gameState = gameState,
                        uiState = uiState.copy(isFlipped = isBoardFlipped),
                        onClick = viewModel::onClick,
                        onDragStart = viewModel::onDragStart,
                        onDrag = viewModel::onDrag,
                        onDragEnd = viewModel::onDragEnd,
                        applyPromotion = viewModel::applyPromotion,
                        cancelPromotion = viewModel::cancelPromotion,
                        onSquareSizeChanged = viewModel::onSquareSizeChanged,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Player: WHITE (or BLACK if board is flipped)
                val bottomSide = if (isBoardFlipped) Side.BLACK else Side.WHITE
                val bottomIsActive = gameState.sideToPlay == bottomSide

                PlayerInfoCard(
                    playerName = if (bottomSide == Side.WHITE) "Player 1 (White)" else "Player 2 (Black)",
                    side = bottomSide,
                    isActive = bottomIsActive,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Control Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flip Board Button
                    Button(
                        onClick = { isBoardFlipped = !isBoardFlipped },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E2C3D),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flip,
                            contentDescription = "Flip Board",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Flip Board",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Reset Game Button
                    Button(
                        onClick = { viewModel.startLocalGame() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart Game",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Restart",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        if (gameState.status == GameStatus.FINISHED) {
            Dialog(
                onDismissRequest = { /* No-op to prevent dismissal */ }
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF111827),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "GAME OVER",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        val statusMsg = buildString {
                            append("Finished")
                            val reason = gameState.reason
                            val result = gameState.result
                            if (!reason.isNullOrBlank() || !result.isNullOrBlank()) {
                                append(": ")
                                if (!reason.isNullOrBlank()) append(reason)
                                if (!result.isNullOrBlank()) append(" ($result)")
                            }
                        }

                        Text(
                            text = statusMsg,
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.startLocalGame() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Play Again", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(48.dp)
                        ) {
                            Text("Leave Match", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerInfoCard(
    playerName: String,
    side: Side,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = modifier.border(
            width = if (isActive) 2.dp else 1.dp,
            color = if (isActive) Color(0xFF4ADE80) else Color(0xFF2D2C3D),
            shape = RoundedCornerShape(16.dp)
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFF1E2F23) else Color(0xFF1C1B22)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar representation
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (side == Side.WHITE) Color.White else Color.Black,
                            shape = CircleShape
                        )
                        .border(
                            width = 1.5.dp,
                            color = Color.Gray,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (side == Side.WHITE) "W" else "B",
                        fontWeight = FontWeight.Bold,
                        color = if (side == Side.WHITE) Color.Black else Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = playerName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Local Player",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            AnimatedVisibility(visible = isActive) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(pulseAlpha)
                            .background(Color(0xFF4ADE80), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "YOUR TURN",
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
