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
    draws: Int,
    winRate: Double = 0.0,
    gold: Int = 0,
    silver: Int = 0,
    bronze: Int = 0
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF262421)
        ),
        border = BorderStroke(1.dp, Color(0xFF312E2B)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Color(0xFF312E2B),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "♟",
                        fontSize = 32.sp,
                        color = Color(0xFFbabfc3)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        text = username,
                        color = Color.White,
                        fontSize = 20.sp,
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
                }
            }

            Spacer(Modifier.height(12.dp))

            // Sub-row: Detailed game stats & Win rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "W: $wins",
                        color = Color(0xFF81B64C),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "L: $losses",
                        color = Color(0xFFF87171),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "D: $draws",
                        color = Color(0xFF9CA3AF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "Tỷ lệ thắng: $winRate%",
                    color = Color(0xFF81B64C),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            // Medals Display Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1C1A17), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🥇", fontSize = 18.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("Vàng: $gold", color = Color(0xFFF5B041), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🥈", fontSize = 18.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("Bạc: $silver", color = Color(0xFFCBD5E1), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🥉", fontSize = 18.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("Đồng: $bronze", color = Color(0xFFB75A14), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}