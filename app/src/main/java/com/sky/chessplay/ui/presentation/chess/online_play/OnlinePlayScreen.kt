package com.sky.chessplay.ui.presentation.chess.online_play

import FriendEvent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.model.chess.DEFAULT_FEN
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.domain.state.FriendState
import com.sky.chessplay.ui.component.online_play.OnlineInfoPanel
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import com.sky.chessplay.ui.layout.TopBarAction
import com.sky.chessplay.ui.presentation.chess.ChessViewModel
import com.sky.chessplay.ui.presentation.chess.online_play.chat.ChatPanel
import com.sky.chessplay.ui.presentation.community.FriendViewModel
import view.board.ChessBoard

@Composable
fun OnlinePlayScreen(
    viewModel: ChessViewModel,
    matchViewModel: MatchViewModel,
    chatViewModel: ChatViewModel = hiltViewModel(),
    friendViewModel: FriendViewModel = hiltViewModel(),
    onlineGameViewModel: OnlineGameViewModel = hiltViewModel(),
    navController: NavHostController,
    authState: AuthState
) {
    val friendState by friendViewModel.state.collectAsState()
    val user = (authState as? AuthState.Authenticated)?.user
    var showChat by remember {
        mutableStateOf(false)
    }
    val gameState = viewModel.gameState
    val uiState = viewModel.uiState
    val gameInit = matchViewModel.gameInitEvent
    val configuration = LocalConfiguration.current
    val rematchOffered = onlineGameViewModel.rematchOffered
    val rematchSent = onlineGameViewModel.rematchSent
    val unreadCount = chatViewModel.unreadCount
    val friendUiState = friendViewModel.uiState
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    LaunchedEffect(friendUiState.statusMessage) {

        friendUiState.statusMessage?.let {

            Toast.makeText(
                context,
                it,
                Toast.LENGTH_SHORT
            ).show()

            friendViewModel.clearStatusMessage()
        }
    }

    LaunchedEffect(gameInit, user?.id) {
        gameInit?.let { init ->
            val fen = init.fen.takeIf { it.isNotBlank() }
                ?: DEFAULT_FEN

            viewModel.startOnlineGame(
                init.copy(fen = fen)
            )
        }
        user?.id?.let {
            chatViewModel.setCurrentUser(it)
        }
    }
    AppScaffold(

        navController = navController,

        config = AppScaffoldConfig(

            title = "Online Match",

            actions = listOf(

                TopBarAction.Chat(
                    unreadCount = unreadCount,
                    onClick = {
                        showChat = true
                        chatViewModel.isChatOpened = true
                        chatViewModel.markAsRead()
                    }
                ),
                TopBarAction.AddFriend(
                    isRequestSent = friendState is FriendState.FriendRequestSent,
                    onClick = {
                        friendViewModel.onEvent(
                            FriendEvent.SendFriendRequest(
                                senderId = user?.id ?: 0,
                                receiverId = gameInit?.opponentId ?: 0
                            )
                        )
                    }
                )
            )
        )

    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center

            ) {

                if (isLandscape) {

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                    ) {

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            ChessBoard(
                                gameState = gameState,
                                uiState = uiState,
                                onClick = viewModel::onClick,
                                onDragStart = viewModel::onDragStart,
                                onDrag = viewModel::onDrag,
                                onDragEnd = viewModel::onDragEnd,
                                applyPromotion = viewModel::applyPromotion,
                                cancelPromotion = viewModel::cancelPromotion,
                                onSquareSizeChanged = viewModel::onSquareSizeChanged
                            )
                        }

                        OnlineInfoPanel(
                            gameState = gameState,
                            viewModel = viewModel,
                            modifier = Modifier
                                .width(260.dp)
                                .fillMaxHeight()
                        )
                    }
                } else {

                    // PORTRAIT
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            OnlineInfoPanel(
                                gameState = gameState,
                                viewModel = viewModel,
                                modifier = Modifier.wrapContentWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                ChessBoard(
                                    gameState = gameState,
                                    uiState = uiState,
                                    onClick = viewModel::onClick,
                                    onDragStart = viewModel::onDragStart,
                                    onDrag = viewModel::onDrag,
                                    onDragEnd = viewModel::onDragEnd,
                                    applyPromotion = viewModel::applyPromotion,
                                    cancelPromotion = viewModel::cancelPromotion,
                                    onSquareSizeChanged = viewModel::onSquareSizeChanged
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showChat) {
            Dialog(
                onDismissRequest = {
                    showChat = false
                    chatViewModel.isChatOpened = false
                }
            ) {

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF111827)
                ) {

                    ChatPanel(
                        messages = chatViewModel.chatMessages,
                        input = chatViewModel.input,
                        onInputChange = chatViewModel::updateInput,
                        onSendClick = {
                            chatViewModel.sendMessage(
                                gameId = matchViewModel.gameId,
                                myName = user?.username ?: "You"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                    )
                }
            }
        }
    }
}