package com.sky.chessplay.ui.component.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun ProfileHeader(
    username: String,
    elo: Int,
    friends: Int,
    gamesPlayed: Int,
    wins: Int,
    losses: Int,
    draws: Int
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF262421)
        ),
        border = BorderStroke(1.dp, Color(0xFF312E2B)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        Color(0xFF312E2B),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♟",
                    fontSize = 36.sp,
                    color = Color(0xFFbabfc3)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = username,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ELO: $elo",
                        color = Color(0xFF81B64C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text("•", color = Color(0xFF62605e))
                    Text(
                        text = "👥 $friends",
                        color = Color(0xFFbabfc3),
                        fontSize = 13.sp
                    )
                    Text("•", color = Color(0xFF62605e))
                    Text(
                        text = "Ván: $gamesPlayed",
                        color = Color(0xFFbabfc3),
                        fontSize = 13.sp
                    )
                }

                Spacer(Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Thắng: $wins",
                        color = Color(0xFF81B64C),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Thua: $losses",
                        color = Color(0xFFF87171),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Hòa: $draws",
                        color = Color(0xFF9CA3AF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}