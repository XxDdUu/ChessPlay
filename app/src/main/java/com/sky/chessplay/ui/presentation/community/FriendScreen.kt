package com.sky.chessplay.ui.presentation.community

import FriendEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.domain.state.FriendState
import com.sky.chessplay.ui.component.friend.FriendActionCard
import com.sky.chessplay.ui.component.friend.FriendItem

@Composable
fun FriendScreen(
    state: FriendState,
    onEvent: (FriendEvent) -> Unit,
    onNavigateToDiscover: () -> Unit
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

            FriendActionCard(
                title = "Tìm bạn",
                icon = Icons.Default.Search,
                onClick = onNavigateToDiscover,
                modifier = Modifier.weight(1f)
            )
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

                        FriendItem(friend)
                    }
                }
            }

            is FriendState.PendingLoaded -> {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(state.pendingRequests) { friend ->

                        FriendItem(friend)
                    }
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
        }
    }
}