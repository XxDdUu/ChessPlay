package com.sky.chessplay.ui.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.ui.component.home.HomeMenuButton

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onMultiplayerClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1B1B)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "♟️ CHESS PLAY ♟️",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        HomeMenuButton("Play", onPlayClick)

        Spacer(Modifier.height(16.dp))

        HomeMenuButton("Multiplayer", onMultiplayerClick)

        Spacer(Modifier.height(16.dp))

        HomeMenuButton("Settings", onSettingsClick)
    }
}
@Composable
@Preview(showBackground = true)
fun PreviewHomeScreen() {
    HomeScreen(
        onPlayClick = {},
        onMultiplayerClick = {},
        onSettingsClick = {}
    )
}