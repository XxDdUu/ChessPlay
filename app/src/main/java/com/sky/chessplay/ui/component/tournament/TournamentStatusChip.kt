package com.sky.chessplay.ui.component.tournament

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TournamentStatusChip(
    status: String,
    modifier: Modifier = Modifier
) {

    val color = when (status) {
        "REGISTERING" -> Color(0xFF4CAF50)
        "ONGOING" -> Color(0xFFFF9800)
        "FINISHED" -> Color(0xFFF44336)
        "UPCOMING" -> Color(0xFF00BCD4)
        "REGISTRATION_CLOSED" -> Color(0xFF9E9E9E)
        else -> MaterialTheme.colorScheme.primary
    }

    val displayText = when (status) {
        "REGISTERING" -> "Mở đăng ký"
        "ONGOING" -> "Đang diễn ra"
        "FINISHED" -> "Đã kết thúc"
        "UPCOMING" -> "Sắp diễn ra"
        "REGISTRATION_CLOSED" -> "Đóng đăng ký"
        else -> status
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Text(
            text = displayText,
            color = color,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            )
        )
    }
}