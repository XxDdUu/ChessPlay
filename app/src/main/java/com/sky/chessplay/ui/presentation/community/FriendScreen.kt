package com.sky.chessplay.ui.presentation.community

import FriendEvent
import FriendEvent.RemoveFriend
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import com.sky.chessplay.data.remote.dto.response.FriendResponse
import com.sky.chessplay.ui.component.friend.FriendItem
import com.sky.chessplay.ui.component.friend.PendingFriendItem
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig

@Composable
fun FriendScreen(
    friends: List<FriendResponse>,
    pendingRequests: List<FriendResponse>,
    isRefreshing: Boolean,
    errorMessage: String?,
    currentUserId: Long,
    onEvent: (FriendEvent) -> Unit,
    onNavigateToDiscover: () -> Unit,
    navController: NavHostController
) {
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
                        OutlinedButton(
                            onClick = onNavigateToDiscover,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF81B64C)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF81B64C)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Tìm bạn mới", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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

                if (pendingRequests.isNotEmpty()) {
                    item {
                        Text(
                            text = "Lời mời kết bạn (${pendingRequests.size})",
                            color = Color(0xFFbabfc3),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(pendingRequests) { request ->
                        PendingFriendItem(
                            request = request,
                            onAccept = {
                                onEvent(
                                    FriendEvent.AcceptFriendRequest(
                                        user1 = request.userId,
                                        user2 = currentUserId
                                    )
                                )
                            },
                            onReject = {}
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
        }
    }
}