package com.sky.chessplay.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomBar(
    onHomeClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF262421))
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomItem(
            icon = "🏠",
            text = "Trang chủ",
            onClick = onHomeClick
        )
        BottomItem(
            icon = "👥",
            text = "Bạn bè",
            onClick = onCommunityClick
        )
        BottomItem(
            icon = "👤",
            text = "Hồ sơ",
            onClick = onProfileClick
        )
    }
}

@Composable
fun BottomItem(
    icon: String,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(icon, fontSize = 22.sp)
        Text(text, color = Color(0xFFbabfc3), fontSize = 11.sp)
    }
}
