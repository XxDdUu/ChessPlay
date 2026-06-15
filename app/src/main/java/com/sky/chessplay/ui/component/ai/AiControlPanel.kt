package com.sky.chessplay.ui.component.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sky.chessplay.data.remote.dto.response.AiModelInfo
import com.sky.chessplay.domain.model.chess.Side

@Composable
fun AiControlPanel(
    aiModels: List<AiModelInfo>,
    selectedModel: String,
    difficulty: Int,
    playerColor: Side,
    gameStarted: Boolean,
    isLoading: Boolean,

    onSelectModel: (String) -> Unit,
    onDifficultyChange: (Int) -> Unit,
    onPlayerColorChange: (Side) -> Unit,

    onStartGame: () -> Unit,
    onResignGame: () -> Unit
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1A17)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "AI SETTINGS",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // MODEL
            ExposedDropdownMenuBoxSample(
                aiModels = aiModels,
                selectedModel = selectedModel,
                onSelectModel = onSelectModel,
                enabled = !gameStarted
            )

            // DIFFICULTY
            Column {

                Text(
                    text = "Difficulty: $difficulty",
                    color = Color.White
                )

                Slider(
                    value = difficulty.toFloat(),
                    onValueChange = {
                        onDifficultyChange(it.toInt())
                    },
                    valueRange = 1f..10f,
                    steps = 8,
                    enabled = !gameStarted
                )
            }

            // COLOR
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                FilterChip(
                    selected = playerColor == Side.WHITE,
                    onClick = {
                        onPlayerColorChange(Side.WHITE)
                    },
                    label = {
                        Text("White")
                    },
                    enabled = !gameStarted
                )

                FilterChip(
                    selected = playerColor == Side.BLACK,
                    onClick = {
                        onPlayerColorChange(Side.BLACK)
                    },
                    label = {
                        Text("Black")
                    },
                    enabled = !gameStarted
                )
            }

            Button(
                onClick = {
                    if (gameStarted) {
                        onResignGame()
                    } else {
                        onStartGame()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        if (gameStarted) {
                            "Resign"
                        } else {
                            "Start Game"
                        }
                    )
                }
            }
        }
    }
}