package com.sky.chessplay.ui.component.profile

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
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        Color(0xFF312E2B),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "♟",
                    fontSize = 40.sp
                )
            }

            Spacer(Modifier.width(20.dp))

            Column {

                Text(
                    username,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    "ELO: $elo   •   👥 $friends   •   Ván: $gamesPlayed",
                    color = Color.LightGray
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text("W: $wins", color = Color(0xFF81B64C))
                    Text("L: $losses", color = Color.Red)
                    Text("D: $draws", color = Color.Gray)
                }
            }
        }
    }
}