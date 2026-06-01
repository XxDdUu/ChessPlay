package com.sky.chessplay.ui.component.online_play

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val WarningRed  = Color(0xFFEF4444)
private val ActiveGreen = Color(0xFF4ADE80)
private val InactiveGray = Color(0xFF6B7280)

/**
 * Displays a chess clock showing the time remaining for one player.
 *
 * @param seconds   Time remaining in seconds.
 * @param isActive  Whether this clock is currently ticking (player's turn).
 * @param modifier  Modifier applied to the outer [Surface].
 */
@Composable
fun ChessClock(
    seconds: Int,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val isLow = seconds in 1..29

    val textColor by animateColorAsState(
        targetValue = when {
            isLow     -> WarningRed
            isActive  -> ActiveGreen
            else      -> Color.White
        },
        animationSpec = tween(durationMillis = 400),
        label = "clockTextColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isLow    -> WarningRed.copy(alpha = 0.7f)
            isActive -> ActiveGreen.copy(alpha = 0.5f)
            else     -> InactiveGray.copy(alpha = 0.25f)
        },
        animationSpec = tween(durationMillis = 400),
        label = "clockBorderColor"
    )

    val bgColor by animateColorAsState(
        targetValue = when {
            isLow    -> WarningRed.copy(alpha = 0.12f)
            isActive -> ActiveGreen.copy(alpha = 0.08f)
            else     -> Color.Black.copy(alpha = 0.35f)
        },
        animationSpec = tween(durationMillis = 400),
        label = "clockBgColor"
    )

    val shape = RoundedCornerShape(8.dp)

    Surface(
        modifier = modifier
            .border(
                width = if (isActive || isLow) 1.5.dp else 1.dp,
                color = borderColor,
                shape = shape
            ),
        shape = shape,
        color = bgColor,
        tonalElevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = formatClockTime(seconds),
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                letterSpacing = 1.sp
            )
        }
    }
}

/** Formats seconds into `M:SS` string, e.g. 605 → "10:05". */
fun formatClockTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}
