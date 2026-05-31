package com.sky.chessplay.ui.component.replay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReplayContent(
    moves: List<String>,
    currentIndex: Int,
    isPlaying: Boolean,
    currentSpeedMs: Long,
    onIndexChange: (Int) -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSpeedChange: (Long) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(currentIndex) {
        val targetRow = (currentIndex - 1) / 2
        if (targetRow >= 0) {
            listState.animateScrollToItem(targetRow)
        }
    }

    val speedLabel = when (currentSpeedMs) {
        1500L -> "1.0x"
        1000L -> "1.5x"
        500L -> "2.0x"
        else -> "1.0x"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 280.dp, max = 450.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Phân tích ván đấu",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF211F1C))
                .padding(8.dp)
        ) {
            val totalTurns = (moves.size + 1) / 2
            items(totalTurns) { turnIndex ->
                val whiteMoveIndex = turnIndex * 2
                val blackMoveIndex = turnIndex * 2 + 1

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${turnIndex + 1}.",
                        color = Color.Gray,
                        modifier = Modifier.width(36.dp),
                        fontSize = 14.sp
                    )

                    if (whiteMoveIndex < moves.size) {
                        val isSelected = currentIndex == whiteMoveIndex + 1
                        Text(
                            text = moves[whiteMoveIndex],
                            color = if (isSelected) Color(0xFFFFD54F) else Color.LightGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onIndexChange(whiteMoveIndex + 1) }
                                .background(if (isSelected) Color(0xFF363431) else Color.Transparent)
                                .padding(4.dp),
                            fontSize = 15.sp
                        )
                    }

                    if (blackMoveIndex < moves.size) {
                        val isSelected = currentIndex == blackMoveIndex + 1
                        Text(
                            text = moves[blackMoveIndex],
                            color = if (isSelected) Color(0xFFFFD54F) else Color.LightGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onIndexChange(blackMoveIndex + 1) }
                                .background(if (isSelected) Color(0xFF363431) else Color.Transparent)
                                .padding(4.dp),
                            fontSize = 15.sp
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            TextButton(
                onClick = {
                    val nextSpeed = when (currentSpeedMs) {
                        1500L -> 1000L //
                        1000L -> 500L
                        else -> 1500L
                    }
                    onSpeedChange(nextSpeed)
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFFD54F))
            ) {
                Text(text = speedLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onIndexChange(0) }) {
                    Icon(Icons.Default.FastRewind, null, tint = Color.White)
                }

                IconButton(
                    onClick = { if (currentIndex > 0) onIndexChange(currentIndex - 1) },
                    enabled = currentIndex > 0
                ) {
                    Icon(
                        Icons.Default.SkipPrevious, null,
                        tint = if (currentIndex > 0) Color.White else Color.DarkGray
                    )
                }

                FloatingActionButton(
                    onClick = onPlayPauseToggle,
                    containerColor = Color(0xFF45423E),
                    contentColor = Color.White,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                }

                IconButton(
                    onClick = { if (currentIndex < moves.size) onIndexChange(currentIndex + 1) },
                    enabled = currentIndex < moves.size
                ) {
                    Icon(
                        Icons.Default.SkipNext, null,
                        tint = if (currentIndex < moves.size) Color.White else Color.DarkGray
                    )
                }

                IconButton(onClick = { onIndexChange(moves.size) }) {
                    Icon(Icons.Default.FastForward, null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}