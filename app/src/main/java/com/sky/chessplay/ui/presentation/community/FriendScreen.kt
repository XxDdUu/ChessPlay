package com.sky.chessplay.ui.presentation.community

import FriendEvent
import FriendEvent.RemoveFriend
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.state.FriendState
import com.sky.chessplay.ui.component.friend.FriendActionCard
import com.sky.chessplay.ui.component.friend.FriendItem
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import com.sky.chessplay.ui.presentation.community.modal.PendingFriendModal

@Composable
fun FriendScreen(
    state: FriendState,
    currentUserId: Long,
    onEvent: (FriendEvent) -> Unit,
    onNavigateToDiscover: () -> Unit,
    navController: NavHostController
) {
    var showPendingModal by remember {
        mutableStateOf(false)
    }
    val pendingCount =
        (state as? FriendState.PendingLoaded)
            ?.pendingRequests
            ?.size ?: 0
    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            title = "👥 Bạn bè",
            showTopBar = true
        )
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(16.dp)
    ) {

        Text(
            text = "👥 Bạn bè",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            FriendActionCard(
                title = "Kết nối bạn bè",
                icon = Icons.Default.PersonAdd,
                onClick = {},
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier.weight(1f)
            ) {

                FriendActionCard(
                    title = "Tìm bạn",
                    icon = Icons.Default.Search,
                    onClick = {
                        showPendingModal = true
                    }
                )

                if (pendingCount > 0) {

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = pendingCount.toString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            FriendActionCard(
                title = "Gửi thư mời",
                icon = Icons.Default.Mail,
                onClick = {},
                modifier = Modifier.weight(1f)
            )

            FriendActionCard(
                title = "Tạo lời thách đấu",
                icon = Icons.Default.Link,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (state) {

            FriendState.Idle -> {

                Text(
                    text = "Không có dữ liệu",
                    color = Color.Gray
                )
            }

            FriendState.Loading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }

            is FriendState.FriendsLoaded -> {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(state.friends) { friend ->

                        FriendItem(
                            friend = friend,
                            onRemoveFriend = {

                                onEvent(

                                    RemoveFriend(
                                        user1 = currentUserId,
                                        user2 = friend.userId
                                    )
                                )
                            }
                        )
                    }
                }
            }

            is FriendState.PendingLoaded -> {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                }
            }

            is FriendState.Success -> {

                Text(
                    text = state.message,
                    color = Color.Green
                )
            }

            is FriendState.Error -> {

                Text(
                    text = state.message,
                    color = Color.Red
                )
            }

            is FriendState.FriendRequestSent -> TODO()
        }
    }
        if (
            showPendingModal &&
            state is FriendState.PendingLoaded
        ) {

            PendingFriendModal(
                requests = state.pendingRequests,
                onDismiss = {
                    showPendingModal = false
                }
            )
        }
    }
}