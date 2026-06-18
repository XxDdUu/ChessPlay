package com.sky.chessplay.ui.component.tournament

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sky.chessplay.domain.model.tournament.TournamentPairing

@Composable
fun PairingCard(pairing: TournamentPairing) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF262421)),
        border = BorderStroke(1.dp, Color(0xFF312E2B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerColumn(
                    name = pairing.whitePlayerName,
                    rating = pairing.whitePlayerRating,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (pairing.isBye) {
                        "BYE"
                    } else {
                        pairing.result.ifBlank { "vs" }
                    },
                    modifier = Modifier.weight(0.7f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                PlayerColumn(
                    name = pairing.blackPlayerName.ifBlank { "No opponent" },
                    rating = pairing.blackPlayerRating,
                    modifier = Modifier.weight(1f),
                    alignEnd = true
                )
            }

            if (pairing.gameId != null) {
                Text(
                    text = "Game #${pairing.gameId}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}