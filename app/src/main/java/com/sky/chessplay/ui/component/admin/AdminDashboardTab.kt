package com.sky.chessplay.ui.component.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sky.chessplay.data.remote.dto.response.AdminDashboardResponse
import com.sky.chessplay.data.remote.dto.response.UserProfileResponse

@Composable
fun AdminDashboardTab(
    stats: AdminDashboardResponse?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedPlayer by remember {
        mutableStateOf<UserProfileResponse?>(null)
    }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(androidx.compose.ui.Alignment.CenterHorizontally)
            )
        } else if (stats != null) {
            // Stats Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Users",
                    value = stats.totalUsers.toString(),
                    modifier = Modifier.weight(1f),
                    textColor = Color.Yellow
                )
                StatCard(
                    title = "Total Games",
                    value = stats.totalGames.toString(),
                    modifier = Modifier.weight(1f),
                    textColor = Color.Cyan
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Tournaments",
                    value = stats.totalTournaments.toString(),
                    modifier = Modifier.weight(1f),
                    textColor = Color.Red
                )
                StatCard(
                    title = "Online Now",
                    value = stats.onlinePlayersCount.toString(),
                    modifier = Modifier.weight(1f),
                    textColor = Color.Green
                )
            }

            // Top Players Section
            Text(
                text = "Top Players",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            stats.topPlayers.forEach { player ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable {
                            selectedPlayer = player
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = player.username,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "⭐ ${player.rating}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            selectedPlayer?.let { player ->
                AdminPlayerDetailDialog(
                    player = player,
                    onDismiss = {
                        selectedPlayer = null
                    }
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}