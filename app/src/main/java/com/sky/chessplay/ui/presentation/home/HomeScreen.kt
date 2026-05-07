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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.ui.component.home.HomeHeader
import com.sky.chessplay.ui.component.home.HomeMenuButton
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.presentation.auth.AuthViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavHostController,
    onPlayClick: () -> Unit,
    onMultiplayerClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val user = (authState as? AuthState.Authenticated)?.user
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

            HomeHeader(
                username = user?.username ?: "Guest",
                avatarUrl = user?.avatar,
                onSettingsClick = onSettingsClick
            )

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