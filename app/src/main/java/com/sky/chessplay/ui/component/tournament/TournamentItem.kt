package com.sky.chessplay.ui.component.tournament

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sky.chessplay.domain.model.tournament.Tournament
import com.sky.chessplay.domain.state.TournamentStatus

@Composable
fun TournamentItem(
    tournament: Tournament,
    onJoinClick: (Long) -> Unit,
    onStandingsClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isRegistered: Boolean,
    onLeaveClick: (Long) -> Unit
) {
    var showDetailDialog by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDetailDialog = true }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = tournament.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    TournamentStatusChip(
                        status = tournament.status.name,
                        modifier = Modifier.wrapContentWidth()
                    )
                }
                Text("🔄 ${tournament.totalRounds} rounds")
                Text("⏱ ${tournament.timeControl}")
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = tournament.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (tournament.status == TournamentStatus.REGISTERING) {
                    if (isRegistered) {
                        OutlinedButton(
                            onClick = { onLeaveClick(tournament.id) }
                        ) {
                            Text("Leave")
                        }
                    } else {
                        Button(
                            onClick = { onJoinClick(tournament.id) }
                        ) {
                            Text("Join")
                        }
                    }
                }
                OutlinedButton(
                    onClick = {
                        onStandingsClick(tournament.id)
                    }
                ) {
                    Text("Details")
                }
            }
        }
    }

    if (showDetailDialog) {
        TournamentDetailDialog(
            tournament = tournament,
            onDismissRequest = { showDetailDialog = false },
            onJoinClick = onJoinClick,
            onStandingsClick = onStandingsClick,
            onLeaveClick = onLeaveClick,
            isRegistered = isRegistered
        )
    }
}
