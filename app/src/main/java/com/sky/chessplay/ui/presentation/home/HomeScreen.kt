package com.sky.chessplay.ui.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sky.chessplay.ui.component.home.HomeHeader
import com.sky.chessplay.ui.component.home.HomeMenuButton
import com.sky.chessplay.ui.layout.AppScaffold

@Composable
fun HomeScreen(
    navController: NavHostController,
    onPlayClick: () -> Unit,
    onMultiplayerClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    AppScaffold(
        navController = navController,
        showBottomBar = true,
        showFab = false
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D0B2A))
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(Modifier.height(16.dp))

            HomeHeader(onSettingsClick)

            Spacer(Modifier.height(24.dp))

            Text(
                text = "CHESS PLAY",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                HomeMenuButton(
                    text = "Play Online",
                    onClick = onPlayClick,
                    modifier = Modifier.fillMaxWidth()
                )

                HomeMenuButton(
                    text = "Daily Puzzle",
                    onClick = {}
                )

                HomeMenuButton(
                    text = "YOU vs AI",
                    onClick = {}
                )

                HomeMenuButton(
                    text = "Play Human",
                    onClick = onMultiplayerClick
                )

                HomeMenuButton(
                    text = "Settings",
                    onClick = onSettingsClick
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewHomeScreen() {
}