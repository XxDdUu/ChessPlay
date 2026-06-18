package com.sky.chessplay.ui.component.tournament

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LeaderboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
        Text("Người chơi", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
        Text("Elo", modifier = Modifier.weight(0.9f), fontWeight = FontWeight.Bold)
        Text("Điểm", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
        Text("BH", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
        Text("SB", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
    }
}