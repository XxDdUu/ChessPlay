package com.sky.chessplay.ui.component.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sky.chessplay.data.remote.dto.response.AiModelInfo
import com.sky.chessplay.domain.model.chess.Side

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSetupModal(
    show: Boolean,

    aiModels: List<AiModelInfo>,
    selectedModel: String,
    difficulty: Int,
    playerColor: Side,

    isLoading: Boolean,

    onDismiss: () -> Unit,
    onSelectModel: (String) -> Unit,
    onDifficultyChange: (Int) -> Unit,
    onPlayerColorChange: (Side) -> Unit,
    onStartGame: () -> Unit
) {

    if (!show) return

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1C1A17),
            border = BorderStroke(1.dp, Color(0xFF312E2B)),
            tonalElevation = 8.dp
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Text(
                    text = "Play vs AI",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                AiControlPanel(
                    aiModels = aiModels,
                    selectedModel = selectedModel,
                    difficulty = difficulty,
                    playerColor = playerColor,
                    gameStarted = false,
                    isLoading = isLoading,
                    onSelectModel = onSelectModel,
                    onDifficultyChange = onDifficultyChange,
                    onPlayerColorChange = onPlayerColorChange,
                    onStartGame = onStartGame,
                    onResignGame = {}
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = onStartGame,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Start Game")
                        }
                    }
                }
            }
        }
    }
}