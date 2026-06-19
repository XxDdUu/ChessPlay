package com.sky.chessplay.ui.presentation.community

import FriendEvent
import FriendEvent.RemoveFriend
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sky.chessplay.data.remote.dto.response.FriendResponse
import com.sky.chessplay.ui.component.friend.FriendItem
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import com.sky.chessplay.ui.presentation.community.modal.PendingFriendModal

@Composable
fun FriendScreen(
    friends: List<FriendResponse>,
    pendingRequests: List<FriendResponse>,
    searchResults: List<com.sky.chessplay.data.remote.dto.response.UserSearchResponse>,
    isSearching: Boolean,
    isRefreshing: Boolean,
    errorMessage: String?,
    currentUserId: Long,
    onEvent: (FriendEvent) -> Unit,
    navController: NavHostController
) {
    var showPendingDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            showTopBar = false,
            showBottomBar = true
        )
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C1A17))
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bạn bè 👥",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BadgedBox(
                                badge = {
                                    if (pendingRequests.isNotEmpty()) {
                                        Badge {
                                            Text("${pendingRequests.size}")
                                        }
                                    }
                                }
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        showPendingDialog = true
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFbabfc3)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF30363D)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Lời mời",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    showSearchDialog = true
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF81B64C)
                                ),
                                border = BorderStroke(1.dp, Color(0xFF81B64C)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Tìm bạn",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (friends.isNotEmpty()) {
                    item {
                        Text(
                            text = "Tất cả bạn bè (${friends.size})",
                            color = Color(0xFFbabfc3),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(friends) { friend ->
                        FriendItem(
                            friend = friend,
                            onRemoveFriend = {
                                onEvent(
                                    RemoveFriend(
                                        user1 = currentUserId,
                                        user2 = friend.userId
                                    )
                                )
                            },
                            onChallengeClick = {
                                onEvent(
                                    FriendEvent.SendChallenge(
                                        friend.userId,
                                        "rapid"
                                    )
                                )
                            }
                        )
                    }
                }

                if (friends.isEmpty() && pendingRequests.isEmpty() && !isRefreshing) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Chưa có bạn bè nào.\nBấm 'Tìm bạn mới' để kết nối!",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            if (isRefreshing && friends.isEmpty() && pendingRequests.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF81B64C)
                )
            }
            if (showPendingDialog) {
                PendingFriendModal(
                    requests = pendingRequests,
                    onAccept = { friend ->
                        onEvent(FriendEvent.AcceptFriendRequest(currentUserId,friend.userId))
                    },
                    onDismiss = {
                        showPendingDialog = false
                    }
                )
            }
            if (showSearchDialog) {
                com.sky.chessplay.ui.presentation.community.modal.SearchFriendModal(
                    searchResults = searchResults,
                    isSearching = isSearching,
                    currentUserId = currentUserId,
                    onSearch = { query ->
                        onEvent(FriendEvent.SearchFriend(query))
                    },
                    onSendRequest = { targetId ->
                        onEvent(FriendEvent.SendFriendRequest(currentUserId, targetId))
                    },
                    onAcceptRequest = { targetId ->
                        onEvent(FriendEvent.AcceptFriendRequest(currentUserId, targetId))
                    },
                    onDismiss = {
                        showSearchDialog = false
                    }
                )
            }
        }
    }
}