package com.sky.chessplay.ui.presentation.community

import FriendEvent
import FriendEvent.RemoveFriend
import androidx.compose.material3.Button
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Column
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
import com.sky.chessplay.ui.presentation.community.modal.SearchFriendModal

@Composable
fun FriendScreen(
    friends: List<FriendResponse>,
    pendingRequests: List<FriendResponse>,
    searchResults: List<com.sky.chessplay.data.remote.dto.response.UserSearchResponse>,
    isSearching: Boolean,
    isRefreshing: Boolean,
    errorMessage: String?,
    currentUserId: Long,
    leaderboard: List<com.sky.chessplay.data.remote.dto.response.LeaderboardResponse> = emptyList(),
    selectedPlayerStats: com.sky.chessplay.data.remote.dto.response.UserProfileResponse? = null,
    isPlayerStatsLoading: Boolean = false,
    onPlayerClick: (Long) -> Unit = {},
    onDismissPlayerStats: () -> Unit = {},
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
                            },
                            onClick = {
                                onPlayerClick(friend.userId)
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

                if (leaderboard.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Bảng xếp hạng Cúp Vàng 🏆",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    items(leaderboard.take(10).mapIndexed { index, item -> index to item }) { (index, player) ->
                        Card(
                            onClick = { onPlayerClick(player.userId.toLong()) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (player.userId.toLong() == currentUserId) Color(0xFF2C2415) else Color(0xFF262421)
                            ),
                            border = if (player.userId.toLong() == currentUserId) BorderStroke(1.dp, Color(0xFFfbbf24)) else null,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    val rankColor = when (index) {
                                        0 -> Color(0xFFfbbf24)
                                        1 -> Color(0xFF94a3b8)
                                        2 -> Color(0xFFb45309)
                                        else -> Color(0xFFbabfc3)
                                    }
                                    Text(
                                        text = "${index + 1}",
                                        color = rankColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.width(20.dp)
                                    )
                                    Text(
                                        text = player.username,
                                        color = if (player.userId.toLong() == currentUserId) Color(0xFFfbbf24) else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    if (player.countryCode != null) {
                                        val flag = if (player.countryCode == "VN") "🇻🇳" else if (player.countryCode == "US") "🇺🇸" else "🌍"
                                        Text(text = flag, fontSize = 14.sp)
                                    }
                                }
                                Text(
                                    text = "⭐ ${player.rating}",
                                    color = Color(0xFFa5b4fc),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
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
                SearchFriendModal(
                    searchResults = searchResults,
                    isLoading = isSearching,
                    currentUserId = currentUserId,
                    onDismiss = {
                        onEvent(FriendEvent.ClearSearchResults)
                        showSearchDialog = false
                    },
                    onEvent = onEvent
                )
            }

            if (isPlayerStatsLoading) {
                Dialog(onDismissRequest = {}) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFF151412), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF81B64C))
                    }
                }
            }

            if (selectedPlayerStats != null) {
                UserProfileModal(
                    stats = selectedPlayerStats,
                    onDismiss = onDismissPlayerStats,
                    currentUserId = currentUserId,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
fun UserProfileModal(
    stats: com.sky.chessplay.data.remote.dto.response.UserProfileResponse,
    onDismiss: () -> Unit,
    currentUserId: Long,
    onEvent: (FriendEvent) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF151412),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF81B64C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stats.username.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Column {
                            val flag = if (stats.countryCode == "VN") "🇻🇳" else if (stats.countryCode == "US") "🇺🇸" else "🌍"
                            Text(
                                text = "${stats.username} $flag",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "⭐ ${stats.rating} ELO",
                                color = Color(0xFFFFC107),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                // Medals section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF262421), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🥇", fontSize = 20.sp)
                        Text("${stats.goldMedals ?: 0} Vàng", color = Color(0xFFfbbf24), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🥈", fontSize = 20.sp)
                        Text("${stats.silverMedals ?: 0} Bạc", color = Color(0xFF94a3b8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🥉", fontSize = 20.sp)
                        Text("${stats.bronzeMedals ?: 0} Đồng", color = Color(0xFFb45309), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Friend Action Button
                stats.friendshipStatus?.let { status ->
                    if (status != "OWNER") {
                        val targetId = stats.userId.toLong()
                        when (status) {
                            "NONE" -> {
                                Button(
                                    onClick = { onEvent(FriendEvent.SendFriendRequest(currentUserId, targetId)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("➕ Kết bạn", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            "PENDING_SENT" -> {
                                Button(
                                    onClick = {},
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF262421),
                                        disabledContainerColor = Color(0xFF262421)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("⏳ Đã gửi lời mời (Đang chờ)", color = Color.Gray, fontWeight = FontWeight.Bold)
                                }
                            }
                            "PENDING_RECEIVED" -> {
                                Button(
                                    onClick = { onEvent(FriendEvent.AcceptFriendRequest(currentUserId, targetId)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("✓ Chấp nhận kết bạn", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            "ACCEPTED" -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF10B981).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✓ Bạn bè", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Match stats
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "THỐNG KÊ KẾT QUẢ",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Số ván đã chơi", color = Color.LightGray, fontSize = 14.sp)
                        Text("${stats.gamesPlayed} ván", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tỉ lệ thắng", color = Color.LightGray, fontSize = 14.sp)
                        Text("${(stats.winRate ?: 0.0).let { "%.1f".format(it) }}%", color = Color(0xFF81B64C), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Thắng / Thua / Hòa", color = Color.LightGray, fontSize = 14.sp)
                        Text(
                            text = "${stats.wins}W / ${stats.losses}L / ${stats.draws}D",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Tournament history
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "LỊCH SỬ GIẢI ĐẤU",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (stats.tournamentHistory.isNullOrEmpty()) {
                        Text(
                            text = "Chưa tham gia giải đấu nào",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        ) {
                            items(stats.tournamentHistory) { t ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF262421), RoundedCornerShape(6.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(t.tournamentName, color = Color.White, fontSize = 13.sp)
                                    val rankColor = when (t.rank) {
                                        1 -> Color(0xFFfbbf24)
                                        2 -> Color(0xFF94a3b8)
                                        3 -> Color(0xFFb45309)
                                        else -> Color.LightGray
                                    }
                                    Text("Hạng ${t.rank}", color = rankColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}