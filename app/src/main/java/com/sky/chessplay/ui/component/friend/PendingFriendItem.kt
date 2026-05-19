package com.sky.chessplay.ui.component.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.data.remote.dto.response.FriendResponse

@Composable
fun PendingFriendItem(
    request: FriendResponse,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF161B22),
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Avatar fake
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFF30363D)),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = request.username.first().uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = request.username,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "⭐ ${request.rating}",
                    color = Color(0xFFFFD54F),
                    fontSize = 13.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFFF9800))
                        .padding(
                            horizontal = 8.dp,
                            vertical = 2.dp
                        )
                ) {

                    Text(
                        text = request.status,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "đã gửi lời mời kết bạn",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FriendActionButton(
                text = "✓",
                background = Color(0xFF2E7D32),
                onClick = onAccept
            )

            FriendActionButton(
                text = "✕",
                background = Color(0xFFC62828),
                onClick = onReject
            )
        }
    }
}