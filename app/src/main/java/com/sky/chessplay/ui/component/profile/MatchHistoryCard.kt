package com.sky.chessplay.ui.component.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sky.chessplay.domain.model.profile.GameHistoryItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MatchHistoryCard(
    username: String,
    history: List<GameHistoryItem>,
    onReplayClick: (GameHistoryItem) -> Unit
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF262421)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Lịch sử ván đấu (${history.size})",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            if (history.isEmpty()) {
                Text(
                    text = "Không có ván đấu nào.",
                    color = Color.LightGray
                )
            } else {
                LazyColumn {
                    items(history) { game ->
                        MatchHistoryRow(
                            game = game,
                            username = username,
                            onClick = {
                                onReplayClick(game)
                            }
                        )

                        HorizontalDivider(
                            color = Color(0xFF312E2B)
                        )
                    }
                }
            }
        }
    }
}