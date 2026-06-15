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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.navigation.Route

@Composable
fun BottomBar(
    currentRoute: String?,
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
            selected = currentRoute == Route.Home.route,
            onClick = onHomeClick
        )

        BottomItem(
            icon = "👥",
            text = "Bạn bè",
            selected = currentRoute == Route.Friend.route,
            onClick = onCommunityClick
        )

        BottomItem(
            icon = "👤",
            text = "Hồ sơ",
            selected = currentRoute == Route.Profile.route,
            onClick = onProfileClick
        )
    }
}

@Composable
fun BottomItem(
    icon: String,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) {
        Color(0xFF81B64C)
    } else {
        Color.White
    }

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = icon,
            fontSize = 24.sp
        )

        Text(
            text = text,
            color = color,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
