package com.sky.chessplay.ui.component.tournament

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sky.chessplay.domain.model.tournament.Standing

@Composable
fun StandingItem(
    standing: Standing,
    modifier: Modifier = Modifier
) {

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "#${standing.rank}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = standing.username,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Buchholz: ${standing.buchholz}"
                )

                Text(
                    text = "SB: ${standing.sonnebornBerger}"
                )
            }

            Text(
                text = standing.currentScore.toString(),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}