package com.sky.chessplay.ui.component.profile

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.domain.model.profile.GameHistoryItem
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MatchHistoryRow(
    game: GameHistoryItem,
    username: String,
    onClick: () -> Unit
) {
    Log.d("GAME HISTORY DEBUG", game.toString())
    val isWhite = game.myColor.equals("WHITE", ignoreCase = true)
    val isLocal = game.gameId.startsWith("local_")
    val formattedScore = game.result.replace("-", " - ")

    val cleanResult = game.result.replace(" ", "")
    val matchStatus = if (isLocal) {
        when (cleanResult) {
            "1-0" -> "TRẮNG THẮNG"
            "0-1" -> "ĐEN THẮNG"
            "1/2-1/2", "0.5-0.5" -> "HÒA"
            else -> "CHƯA RÕ"
        }
    } else {
        when {
            cleanResult == "1/2-1/2" || cleanResult == "0.5-0.5" -> "HÒA"
            cleanResult == "1-0" -> if (isWhite) "THẮNG" else "THUA"
            cleanResult == "0-1" -> if (isWhite) "THUA" else "THẮNG"
            else -> "CHƯA RÕ"
        }
    }

    val whitePlayer = if (isLocal) "Người chơi 1 (Trắng)" else if (isWhite) username else (game.opponentName ?: "AI")
    val blackPlayer = if (isLocal) "Người chơi 2 (Đen)" else if (isWhite) (game.opponentName ?: "AI") else username

    val formattedDate = rememberFormattedDate(game.playedAt)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2B28) // Màu nền tối của Card
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ChessColorIndicator(isWhite = true)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = whitePlayer,
                        color = Color.White,
                        fontWeight = if (isWhite && !isLocal) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 15.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ChessColorIndicator(isWhite = false)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = blackPlayer,
                        color = Color.White,
                        fontWeight = if (!isWhite && !isLocal) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 15.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = matchStatus,
                    color = when (matchStatus) {
                        "THẮNG", "TRẮNG THẮNG" -> Color(0xFF4CAF50)
                        "THUA", "ĐEN THẮNG" -> Color(0xFFF44336)
                        else -> Color.LightGray
                    },
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )

                Text(
                    text = formattedDate,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ChessColorIndicator(isWhite: Boolean) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(
                color = if (isWhite) Color.White else Color(0xFF1A1A1A),
                shape = CircleShape
            )
            .then(
                if (isWhite) Modifier // Nếu là quân trắng trên nền tối thì không cần viền
                else Modifier.background(Color(0xFF555555), CircleShape).padding(1.dp).background(Color.Black, CircleShape)
            )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberFormattedDate(dateString: String): String {
    return androidx.compose.runtime.remember(dateString) {
        try {
            val parsedDate = ZonedDateTime.parse(dateString)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
            parsedDate.format(formatter)
        } catch (e: Exception) {
            try {
                if (dateString.toLongOrNull() != null) {
                    val instant = java.time.Instant.ofEpochMilli(dateString.toLong())
                    val zonedDateTime = ZonedDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
                    zonedDateTime.format(formatter)
                } else {
                    dateString
                }
            } catch (ex: Exception) {
                dateString
            }
        }
    }
}