package com.sky.chessplay.ui.component.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.domain.model.profile.UserTournament

@Composable
fun TournamentHistoryCard(
    tournaments: List<UserTournament>
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
                text = "Giải đấu đã tham gia (${tournaments.size})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(12.dp))

            if (tournaments.isEmpty()) {
                Text(
                    text = "Bạn chưa tham gia giải đấu nào.",
                    color = Color(0xFFbabfc3),
                    fontSize = 14.sp
                )
            } else {
                LazyColumn {
                    items(tournaments) { t ->
                        TournamentRow(tournament = t)
                        HorizontalDivider(
                            color = Color(0xFF312E2B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentRow(
    tournament: UserTournament
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1.5f)
        ) {
            Text(
                text = tournament.tournamentName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = tournament.startTime?.split("T")?.firstOrNull() ?: "Không rõ ngày",
                color = Color(0xFF62605e),
                fontSize = 11.sp
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "#${tournament.rank}",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${tournament.score} điểm",
                color = Color(0xFF81B64C),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (medalText, medalColor) = when (tournament.medal?.uppercase()) {
                "GOLD" -> "🥇 Vàng" to Color(0xFFF5B041)
                "SILVER" -> "🥈 Bạc" to Color(0xFFCBD5E1)
                "BRONZE" -> "🥉 Đồng" to Color(0xFFB75A14)
                else -> "-" to Color(0xFF62605e)
            }

            Text(
                text = medalText,
                color = medalColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
