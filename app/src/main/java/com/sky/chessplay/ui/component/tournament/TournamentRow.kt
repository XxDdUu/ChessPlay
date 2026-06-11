package com.sky.chessplay.ui.component.tournament

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sky.chessplay.domain.model.tournament.Tournament

@Composable
fun TournamentRow(
    tournament: Tournament,
    onJoinClick: (Long) -> Unit = {},
    onStandingsClick: (Long) -> Unit = {}
) {
    var showDetailDialog by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDetailDialog = true },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171332))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Tournament",
                tint = Color(0xFFFFD54F),
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(2f)) {
                Text(
                    text = tournament.name,
                    color = Color.White
                )
                Text(
                    text = tournament.description,
                    color = Color(0xFFB0B0C3),
                    maxLines = 1
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = tournament.totalRounds.toString(), color = Color.White)
            }

            Column(modifier = Modifier.weight(1.2f)) {
                Text(text = tournament.timeControl, color = Color.White)
            }

            TournamentStatusChip(
                status = tournament.status.name,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Tournament details",
                tint = Color(0xFF8B8ACA),
                modifier = Modifier
                    .padding(start = 12.dp)
                    .clickable { showDetailDialog = true }
            )
        }
    }

    if (showDetailDialog) {
        TournamentDetailDialog(
            tournament = tournament,
            onDismissRequest = { showDetailDialog = false },
            onJoinClick = onJoinClick,
            onStandingsClick = onStandingsClick,
            isRegistered = false
        )
    }
}
