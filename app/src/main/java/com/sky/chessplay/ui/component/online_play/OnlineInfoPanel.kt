package com.sky.chessplay.ui.component.online_play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.domain.socket.GameStatus
import com.sky.chessplay.domain.socket.SocketEvent
import com.sky.chessplay.ui.component.common.GradientButton
import com.sky.chessplay.ui.presentation.chess.ChessViewModel
import model.state.GameState

@Composable
fun OnlineInfoPanel(
    gameState: GameState,
    viewModel: ChessViewModel,
    opponentTimeSeconds: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MatchHeader(
            statusText = viewModel.buildStatusText(gameState),
            isMyTurn = gameState.isMyTurn,
            modifier = Modifier.fillMaxWidth()
        )

        OpponentSection(
            gameState = gameState,
            opponentTimeSeconds = opponentTimeSeconds,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MatchHeader(
    statusText: String,
    isMyTurn: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = if (isMyTurn) "Your move" else "Opponent move",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = statusText,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
            ) {
                Text(
                    text = if (isMyTurn) "ACTIVE" else "WAITING",
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun OpponentSection(
    gameState: GameState,
    opponentTimeSeconds: Int,
    modifier: Modifier = Modifier
) {
    val isOpponentTurn = !gameState.isMyTurn && gameState.status == GameStatus.PLAYING
    val opponentSide =
        gameState.mySide
            ?.opposite
            ?.name
            ?: "--"
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = gameState.opponent
                        ?.name
                        ?.firstOrNull()
                        ?.uppercase() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name + rating
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = gameState.opponent?.name
                        ?: "Waiting...",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Text(
                    text = "${gameState.opponent?.rating ?: "--"} • $opponentSide",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            ChessClock(
                seconds = opponentTimeSeconds,
                isActive = isOpponentTurn
            )
        }
    }
}

@Composable
fun PlayerSection(
    gameState: GameState,
    myTimeSeconds: Int,
    modifier: Modifier = Modifier
) {
    val isMyTurn = gameState.isMyTurn && gameState.status == GameStatus.PLAYING
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "You",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Side: ${gameState.mySide?.name ?: "Unknown"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ChessClock(
                seconds = myTimeSeconds,
                isActive = isMyTurn
            )
        }
    }
}

@Composable
fun BottomActions(
    rematchOffered: Boolean,
    rematchSent: Boolean,
    onChatClick: () -> Unit,
    onResign: () -> Unit,
    onOfferDraw: () -> Unit,
    onOfferRematch: () -> Unit,
    onAcceptRematch: () -> Unit,
    onRejectRematch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GradientButton(
                title = "Chat",
                icon = Icons.Default.Chat,
                onClick = onChatClick,
                modifier = Modifier.weight(1f),
                colors = listOf(
                    Color(0xFF4FACFE),
                    Color(0xFF00F2FE)
                )
            )

            GradientButton(
                title = "Rematch",
                icon = Icons.Default.Refresh,
                onClick = onOfferRematch,
                modifier = Modifier.weight(1f),
                enabled = !rematchSent && !rematchOffered,
                colors = listOf(
                    Color(0xFF43E97B),
                    Color(0xFF38F9D7)
                )
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GradientButton(
                title = "Draw",
                icon = Icons.Default.Handshake,
                onClick = onOfferDraw,
                modifier = Modifier.weight(1f),
                colors = listOf(
                    Color(0xFFFFC107),
                    Color(0xFFFF9800)
                )
            )

            GradientButton(
                title = "Resign",
                icon = Icons.Default.Flag,
                onClick = onResign,
                modifier = Modifier.weight(1f),
                colors = listOf(
                    Color(0xFFFF416C),
                    Color(0xFFFF4B2B)
                )
            )
        }
    }

        if (rematchOffered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAcceptRematch,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(48.dp)
                ) {
                    Text("Accept")
                }
                OutlinedButton(
                    onClick = onRejectRematch,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reject")
                }
            }
            Text(
                text = "Opponent offered a rematch.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        } else if (rematchSent) {
            Text(
                text = "Rematch request sent.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
    }
}
