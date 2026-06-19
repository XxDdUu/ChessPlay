package com.sky.chessplay.ui.presentation.chess.online_play

import FriendEvent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sky.chessplay.R
import com.sky.chessplay.domain.model.chess.DEFAULT_FEN
import com.sky.chessplay.domain.model.chess.Side
import com.sky.chessplay.domain.socket.GameStatus
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.domain.state.FriendState
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.component.online_play.BottomActions
import com.sky.chessplay.ui.component.online_play.OnlineInfoPanel
import com.sky.chessplay.ui.component.online_play.PlayerSection
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import com.sky.chessplay.ui.layout.TopBarAction
import com.sky.chessplay.ui.presentation.chess.ChessViewModel
import com.sky.chessplay.ui.presentation.chess.online_play.chat.ChatPanel
import com.sky.chessplay.ui.presentation.community.FriendViewModel
import kotlinx.coroutines.delay
import view.board.ChessBoard

@Composable
fun OnlinePlayScreen(
    viewModel: ChessViewModel,
    matchViewModel: MatchViewModel,
    chatViewModel: ChatViewModel = hiltViewModel(),
    friendViewModel: FriendViewModel = hiltViewModel(),
    onlineGameViewModel: OnlineGameViewModel = hiltViewModel(),
    navController: NavHostController,
    authState: AuthState,
    isTournament: Boolean = false,
    tournamentId: Long? = null
) {
    LaunchedEffect(isTournament, tournamentId) {
        onlineGameViewModel.isTournament = isTournament
        onlineGameViewModel.tournamentId = tournamentId
    }

    LaunchedEffect(onlineGameViewModel.gameOverResult) {
        if (isTournament && tournamentId != null && onlineGameViewModel.gameOverResult != null) {
            delay(5000)
            navController.navigate(Route.TournamentDetail.createRoute(tournamentId)) {
                popUpTo(Route.OnlinePlay.route) { this.inclusive = true }
            }
        }
    }
    val friendState by friendViewModel.state.collectAsState()
    val user = (authState as? AuthState.Authenticated)?.user
    var showChat by remember {
        mutableStateOf(false)
    }
    val gameState = viewModel.gameState
    val uiState = viewModel.uiState

    LaunchedEffect(gameState.sideToPlay, gameState.status) {
        if (gameState.status == GameStatus.PLAYING) {
            onlineGameViewModel.setActiveSide(gameState.sideToPlay)
        } else {
            onlineGameViewModel.setActiveSide(null)
        }
    }

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
            onlineGameViewModel.initializeGame(init, viewModel.gameState.sideToPlay)
        }
        user?.id?.let {
            chatViewModel.setCurrentUser(it)
        }
    }

    LaunchedEffect(user?.id, gameInit?.opponentId) {
        user?.id?.let { uid ->
            friendViewModel.onEvent(FriendEvent.LoadFriends(uid))
        }
    }

    // True when the opponent is already in the friends list
    val isAlreadyFriend = remember(friendViewModel.friendsList, gameInit?.opponentId) {
        val opponentId = gameInit?.opponentId
        opponentId != null && friendViewModel.friendsList.any { it.userId == opponentId }
    }

    AppScaffold(

        navController = navController,

        config = AppScaffoldConfig(

            title = "Trận đấu trực tuyến",

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
                    isAlreadyFriend = isAlreadyFriend,
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
                    .padding(horizontal = 8.dp, vertical = 4.dp),
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

                            Spacer(modifier = Modifier.height(6.dp))

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

                            Spacer(modifier = Modifier.height(4.dp))

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
                                myName = user?.username ?: "Bạn"
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
                            text = "ĐỀ NGHỊ HÒA",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Đối thủ đề nghị một ván hòa.",
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
                                Text("Chấp nhận")
                            }
                            OutlinedButton(
                                onClick = {
                                    matchViewModel.gameId?.let { onlineGameViewModel.rejectDraw(it) }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(48.dp)
                            ) {
                                Text("Từ chối")
                            }
                        }
                    }
                }
            }
        }

        if (gameState.status == GameStatus.FINISHED) {
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
                        val resultStr = onlineGameViewModel.gameOverResult?.uppercase() ?: ""
                        val mySide = gameState.mySide
                        val isWin = (mySide == Side.WHITE && resultStr == "WHITE_WIN") || 
                                    (mySide == Side.BLACK && resultStr == "BLACK_WIN")
                        val isLoss = (mySide == Side.WHITE && resultStr == "BLACK_WIN") || 
                                     (mySide == Side.BLACK && resultStr == "WHITE_WIN")

                        if (isWin) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_golden_cup),
                                contentDescription = "Cup Vàng",
                                modifier = Modifier.size(100.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "BẠN ĐÃ CHIẾN THẮNG",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24),
                                textAlign = TextAlign.Center
                            )
                        } else if (isLoss) {
                            val winnerText = if (resultStr == "WHITE_WIN") "Trắng thắng" else "Đen thắng"
                            Text(
                                text = winnerText,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = "VÁN ĐẤU KẾT THÚC",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }

                        val statusMsg = buildString {
                            val reason = onlineGameViewModel.gameOverReason
                            val result = onlineGameViewModel.gameOverResult
                            if (!reason.isNullOrBlank() || !result.isNullOrBlank()) {
                                if (!reason.isNullOrBlank()) {
                                    val translatedReason = when (reason.uppercase()) {
                                        "CHECKMATE" -> "Chiếu hết"
                                        "RESIGNATION" -> "Đầu hàng"
                                        "TIMEOUT" -> "Hết thời gian"
                                        "STALEMATE" -> "Hòa Stalemate"
                                        "INSUFFICIENT_MATERIAL" -> "Hòa do không đủ lực lượng"
                                        "FIFTY_MOVES" -> "Hòa luật 50 nước đi"
                                        "REPETITION" -> "Hòa lặp lại 3 lần"
                                        "AGREEMENT" -> "Hòa do thỏa thuận"
                                        else -> reason
                                    }
                                    append(translatedReason)
                                }
                                if (!result.isNullOrBlank()) {
                                    val translatedResult = when (result.uppercase()) {
                                        "WHITE_WIN" -> "Trắng thắng"
                                        "BLACK_WIN" -> "Đen thắng"
                                        "DRAW" -> "Hòa"
                                        else -> result
                                    }
                                    if (reason.isNullOrBlank()) append(translatedResult) else append(" ($translatedResult)")
                                }
                            } else {
                                append("Đã kết thúc")
                            }
                        }

                        Text(
                            text = statusMsg,
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (!isTournament) {
                            if (rematchOffered) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Đối thủ đề nghị đấu lại!",
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
                                            Text("Chấp nhận")
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                matchViewModel.gameId?.let { onlineGameViewModel.rejectRematch(it) }
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(48.dp)
                                        ) {
                                            Text("Từ chối")
                                        }
                                    }
                                }
                            } else if (rematchSent) {
                                Text(
                                    text = "Đã gửi đề xuất đấu lại...",
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
                                    Text("Đề xuất đấu lại")
                                }
                            }
                        } else {
                            Text(
                                text = "Đang chuyển hướng về BXH giải đấu trong vài giây...",
                                color = Color(0xFFFFB300),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        if (isTournament && tournamentId != null) {
                            Button(
                                onClick = {
                                    navController.navigate(Route.TournamentDetail.createRoute(tournamentId)) {
                                        popUpTo(Route.OnlinePlay.route) { this.inclusive = true }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(48.dp)
                            ) {
                                Text("Quay lại BXH giải đấu")
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    navController.popBackStack()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(48.dp)
                            ) {
                                Text("Rời trận đấu")
                            }
                        }
                    }
                }
            }
        }
    }
}