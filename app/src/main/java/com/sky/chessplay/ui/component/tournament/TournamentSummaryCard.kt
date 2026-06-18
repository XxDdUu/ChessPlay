package com.sky.chessplay.ui.component.tournament

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sky.chessplay.domain.model.tournament.Tournament
import com.sky.chessplay.domain.state.TournamentStatus
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TournamentSummaryCard(
    tournament: Tournament,
    isRegistered: Boolean,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit,
    myPairing: com.sky.chessplay.domain.model.tournament.MyPairing? = null,
    onLobbyClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF262421)
        ),
        border = BorderStroke(1.dp, Color(0xFF312E2B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tournament.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tournament.description.ifBlank { "Không có mô tả." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TournamentStatusChip(status = tournament.status.name)
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailStat(
                    label = "Thời gian",
                    value = tournament.timeControl,
                    modifier = Modifier.weight(1f)
                )
                DetailStat(
                    label = "Số ván đấu",
                    value = tournament.totalRounds.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailStat(
                    label = "Mở đăng ký",
                    value = formatTournamentDate(tournament.registrationStart),
                    modifier = Modifier.weight(1f)
                )
                DetailStat(
                    label = "Đóng đăng ký",
                    value = formatTournamentDate(tournament.registrationEnd),
                    modifier = Modifier.weight(1f)
                )
            }

            DetailStat(
                label = "Bắt đầu giải đấu",
                value = formatTournamentDate(tournament.startTime),
                modifier = Modifier.fillMaxWidth()
            )

            if (tournament.status == TournamentStatus.REGISTERING) {
                if (isRegistered) {
                    OutlinedButton(
                        onClick = onLeaveClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Rời giải đấu")
                    }
                } else {
                    Button(
                        onClick = onJoinClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tham gia giải đấu")
                    }
                }
            } else if (tournament.status == TournamentStatus.ONGOING && isRegistered) {
                if (myPairing != null) {
                    val isBreak = myPairing.inBreak
                    Button(
                        onClick = onLobbyClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBreak) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBreak) "Giải lao giữa hiệp (Round ${myPairing.roundNumber})" else "Vào phòng chờ thi đấu (Round ${myPairing.roundNumber})")
                    }
                } else {
                    Text(
                        text = "Đang đợi hệ thống ghép cặp vòng đấu mới...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
@Composable
private fun DetailStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
private fun formatTournamentDate(dateString: String): String {
    if (dateString.isBlank()) return "Chưa thiết lập"
    return try {
        val parsedDate = ZonedDateTime.parse(dateString).withZoneSameInstant(java.time.ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
        parsedDate.format(formatter)
    } catch (e: Exception) {
        try {
            val parsed = java.time.OffsetDateTime.parse(dateString).atZoneSameInstant(java.time.ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
            parsed.format(formatter)
        } catch (ex: Exception) {
            dateString
        }
    }
}
