package com.sky.chessplay.ui.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sky.chessplay.data.remote.dto.response.AiModelInfo
import com.sky.chessplay.domain.model.auth.Role
import com.sky.chessplay.domain.model.chess.Side
import com.sky.chessplay.domain.socket.MatchEvent
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.component.admin.RainbowButton
import com.sky.chessplay.ui.component.ai.AiSetupModal
import com.sky.chessplay.ui.component.home.HomeHeader
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
    val scrollState = rememberScrollState()

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
                .background(Color(0xFF1C1A17))
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(20.dp))

            HomeHeader(
                username = user?.username ?: "Guest",
                avatarUrl = user?.avatar,
                onLogout = onLogout,
                onProfileClick = {
                    navController.navigate(Route.Profile.route)
                }
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "🏆 SO TÀI CỜ VUA",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GameModeCard(
                    title = "Chơi trực tuyến",
                    description = "Chơi với đối thủ cùng trình độ ngẫu nhiên",
                    emoji = "⚡",
                    accentColor = Color(0xFF81b64c),
                    onClick = onMultiplayerClick
                )

                GameModeCard(
                    title = "Chơi với Bot",
                    description = "Thử thách với mạng nơ-ron AI AlphaOne",
                    emoji = "🤖",
                    accentColor = Color(0xFF3B82F6),
                    onClick = onOpenAiModal
                )

                GameModeCard(
                    title = "Giải đấu cờ vua",
                    description = "Tham gia tranh tài các giải đấu trực tuyến",
                    emoji = "🏆",
                    accentColor = Color(0xFFFBBF24),
                    onClick = onTournamentClick
                )

                GameModeCard(
                    title = "Chơi ngoại tuyến",
                    description = "Chơi với bạn bè hoặc luyện tập tại chỗ",
                    emoji = "👥",
                    accentColor = Color(0xFFD97706),
                    onClick = onPlayClick
                )

                if (user?.role == Role.ROLE_ADMIN) {
                    Spacer(Modifier.height(8.dp))
                    RainbowButton(
                        text = "👑 Admin Panel",
                        onClick = onAdminClick
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
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
fun GameModeCard(
    title: String,
    description: String,
    emoji: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF262421)
        ),
        border = BorderStroke(1.dp, Color(0xFF312E2B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = Color(0xFFbabfc3),
                    fontSize = 12.sp
                )
            }

            Text(
                text = "➔",
                color = Color(0xFF62605e),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}