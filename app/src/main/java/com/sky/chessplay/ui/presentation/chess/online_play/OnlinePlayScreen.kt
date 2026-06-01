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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.model.chess.DEFAULT_FEN
import com.sky.chessplay.domain.model.chess.Side
import com.sky.chessplay.domain.socket.SocketEvent
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.domain.state.FriendState
import com.sky.chessplay.ui.component.online_play.BottomActions
import com.sky.chessplay.ui.component.online_play.OnlineInfoPanel
import com.sky.chessplay.ui.component.online_play.PlayerSection
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

    // --- Clock values ---
    val mySide = gameState.mySide
    val myTimeSeconds = if (mySide == Side.WHITE)
        onlineGameViewModel.whiteTimeSeconds
    else
        onlineGameViewModel.blackTimeSeconds
    val opponentTimeSeconds = if (mySide == Side.WHITE)
        onlineGameViewModel.blackTimeSeconds
    else
        onlineGameViewModel.whiteTimeSeconds
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
                                opponentTimeSeconds = opponentTimeSeconds,
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
                                .fillMaxSize()
                                .padding(bottom = 8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            OnlineInfoPanel(
                                gameState = gameState,
                                viewModel = viewModel,
                                opponentTimeSeconds = opponentTimeSeconds,
                                modifier = Modifier.wrapContentWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
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

                            PlayerSection(
                                gameState = gameState,
                                myTimeSeconds = myTimeSeconds,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            BottomActions(
                                rematchOffered = rematchOffered,
                                rematchSent = rematchSent,
                                onChatClick = {
                                    showChat = true
                                    chatViewModel.isChatOpened = true
                                    chatViewModel.markAsRead()
                                },
                                onOfferRematch = {
                                    matchViewModel.gameId?.let(onlineGameViewModel::offerRematch)
                                },
                                onAcceptRematch = {
                                    matchViewModel.gameId?.let(onlineGameViewModel::acceptRematch)
                                },
                                onRejectRematch = {
                                    matchViewModel.gameId?.let(onlineGameViewModel::rejectRematch)
                                },
                                onResign = {
                                    matchViewModel.gameId?.let(onlineGameViewModel::resign)
                                },
                                onOfferDraw = {
                                    matchViewModel.gameId?.let(onlineGameViewModel::offerDraw)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
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

        if (onlineGameViewModel.drawOffered) {
            Dialog(onDismissRequest = { /* prevent dismiss */ }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF111827),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "DRAW OFFER",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Opponent offered a draw.",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    matchViewModel.gameId?.let { onlineGameViewModel.acceptDraw(it) }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(48.dp)
                            ) {
                                Text("Accept")
                            }
                            OutlinedButton(
                                onClick = {
                                    matchViewModel.gameId?.let { onlineGameViewModel.rejectDraw(it) }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(48.dp)
                            ) {
                                Text("Decline")
                            }
                        }
                    }
                }
            }
        }

        if (gameState.status == SocketEvent.GameStatus.FINISHED) {
            Dialog(
                onDismissRequest = { /* No-op to prevent dismissal */ }
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF111827),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "GAME OVER",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        val statusMsg = buildString {
                            append("Finished")
                            val reason = onlineGameViewModel.gameOverReason
                            val result = onlineGameViewModel.gameOverResult
                            if (!reason.isNullOrBlank() || !result.isNullOrBlank()) {
                                append(": ")
                                if (!reason.isNullOrBlank()) append(reason)
                                if (!result.isNullOrBlank()) append(" ($result)")
                            }
                        }

                        Text(
                            text = statusMsg,
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (rematchOffered) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Opponent offered a rematch!",
                                    color = Color(0xFF4ADE80),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            matchViewModel.gameId?.let { onlineGameViewModel.acceptRematch(it) }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(48.dp)
                                    ) {
                                        Text("Accept")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            matchViewModel.gameId?.let { onlineGameViewModel.rejectRematch(it) }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(48.dp)
                                    ) {
                                        Text("Decline")
                                    }
                                }
                            }
                        } else if (rematchSent) {
                            Text(
                                text = "Rematch offer sent...",
                                color = Color.Gray,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            Button(
                                onClick = {
                                    matchViewModel.gameId?.let { onlineGameViewModel.offerRematch(it) }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(48.dp)
                            ) {
                                Text("Offer Rematch")
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(48.dp)
                        ) {
                            Text("Leave Match")
                        }
                    }
                }
            }
        }
    }
}