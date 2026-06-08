package com.sky.chessplay.ui.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import com.sky.chessplay.data.remote.dto.response.AiModelInfo
import com.sky.chessplay.domain.model.chess.Side
import com.sky.chessplay.domain.socket.MatchEvent
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.component.ai.AiSetupModal
import com.sky.chessplay.ui.component.home.HomeHeader
import com.sky.chessplay.ui.component.home.HomeMenuButton
import com.sky.chessplay.ui.component.online_play.MatchInvitePopup
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig

@Composable
fun HomeScreen(
    authState: AuthState,
    navController: NavHostController,
    onPlayClick: () -> Unit,
    onMultiplayerClick: () -> Unit,
    onSettingsClick: () -> Unit,
    showAiModal: Boolean,
    isLoading: Boolean,
    incomingInvite: MatchEvent.MatchInvite?,

    aiModels: List<AiModelInfo>,
    selectedModel: String,
    difficulty: Int,
    playerColor: Side,

    onOpenAiModal: () -> Unit,
    onDismissAiModal: () -> Unit,

    onSelectModel: (String) -> Unit,
    onDifficultyChange: (Int) -> Unit,
    onPlayerColorChange: (Side) -> Unit,
    onAcceptInvite: (Long) -> Unit,
    onDismissInvite: () -> Unit,

    onStartAiGame: () -> Unit,
    onLogout: () -> Unit,
    onTournamentClick: () -> Unit,
    onAdminClick: () -> Unit,

    ) {
    val user = (authState as? AuthState.Authenticated)?.user
    val invite = incomingInvite
    AppScaffold(

        navController = navController,

        config = AppScaffoldConfig(
            showTopBar = false,
            showBottomBar = true,
            fab = null
        )

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {

            Spacer(Modifier.height(16.dp))

            HomeHeader(
                username = user?.username ?: "Guest",
                avatarUrl = user?.avatar,
                onLogout = onLogout,
                onProfileClick = {
                    navController.navigate(Route.Profile.route)
                }
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Chess Play",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(20.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                HomeMenuButton(
                    text = "Play Offline",
                    onClick = onPlayClick,
                    modifier = Modifier.fillMaxWidth()
                )

                HomeMenuButton(
                    text = "Tournament",
                    onClick = onTournamentClick,
                    modifier = Modifier.fillMaxWidth()
                )

                HomeMenuButton(
                    text = "YOU vs AI",
                    onClick = onOpenAiModal
                )

                HomeMenuButton(
                    text = "Play Online",
                    onClick = onMultiplayerClick
                )

                HomeMenuButton(
                    text = "Admin Panel",
                    onClick = onAdminClick
                )

                HomeMenuButton(
                    text = "Settings",
                    onClick = onSettingsClick
                )
            }
        }
        AiSetupModal(
            show = showAiModal,

            aiModels = aiModels,
            selectedModel = selectedModel,
            difficulty = difficulty,
            playerColor = playerColor,

            isLoading = isLoading,

            onDismiss = onDismissAiModal,

            onSelectModel = onSelectModel,
            onDifficultyChange = onDifficultyChange,
            onPlayerColorChange = onPlayerColorChange,

            onStartGame = {
                onDismissAiModal()
                onStartAiGame()
            }
        )
        MatchInvitePopup(
            visible = invite != null,
            hostName = invite?.hostName ?: "",

            onAccept = {
                incomingInvite?.let {
                    onAcceptInvite(it.hostId)
                }
            },

            onDismiss = onDismissInvite
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewHomeScreen() {
}