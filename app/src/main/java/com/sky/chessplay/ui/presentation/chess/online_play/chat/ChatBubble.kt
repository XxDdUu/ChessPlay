package com.sky.chessplay.ui.presentation.chess.online_play.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.domain.model.chat.ChatMessage

@Composable
fun ChatBubble(
    message: ChatMessage
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment =
            if (message.isMine)
                Alignment.End
            else
                Alignment.Start
    ) {

        Text(
            text = message.senderName,
            fontSize = 12.sp,
            color = Color(0xFF9CA3AF),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .background(
                    color =
                        if (message.isMine)
                            Color(0xFF6D5BFF)
                        else
                            Color(0xFF374151),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
        ) {

            Text(
                text = message.message,
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}