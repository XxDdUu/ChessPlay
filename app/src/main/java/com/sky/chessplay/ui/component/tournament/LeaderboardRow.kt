package com.sky.chessplay.ui.component.tournament

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sky.chessplay.domain.model.tournament.Standing

@Composable
fun LeaderboardRow(
    standing: Standing,
    rank: Int,
    isCurrentUser: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF262421)),
        border = BorderStroke(1.dp, Color(0xFF312E2B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#$rank", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
            Text(
                text = if (isCurrentUser) "${standing.username} (You)" else standing.username,
                modifier = Modifier.weight(2f),
                fontWeight = FontWeight.SemiBold
            )
            Text(standing.initialRating.toString(), modifier = Modifier.weight(0.9f))
            Text(standing.currentScore.toString(), modifier = Modifier.weight(0.8f))
            Text(standing.buchholz.toString(), modifier = Modifier.weight(0.8f))
            Text(standing.sonnebornBerger.toString(), modifier = Modifier.weight(0.8f))
        }
    }
}
