package com.sky.chessplay.ui.presentation.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.model.profile.GameHistoryItem
import com.sky.chessplay.ui.component.profile.ActiveFilterTags
import com.sky.chessplay.ui.component.profile.FilterModal
import com.sky.chessplay.ui.component.profile.MatchHistoryCard
import com.sky.chessplay.ui.component.profile.ProfileHeader
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import com.sky.chessplay.ui.presentation.replay.ReplayScreen

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    state: ProfileState,
    username: String,
    onRefresh: () -> Unit,
    onViewFriends: () -> Unit,
    navController: NavHostController,
    onFilterOpponentChange: (String) -> Unit = {},
    onFilterResultChange: (String) -> Unit = {},
    onResetFilters: () -> Unit = {},
    onHistoryTypeChange: (HistoryType) -> Unit = {}
) {
    var showSearchModal by remember { mutableStateOf(false) }
    var selectedGameForReplay by remember { mutableStateOf<GameHistoryItem?>(null) }
    val replaySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C1A17)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF81B64C))
        }
        return
    }

    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            showTopBar = false,
            showBottomBar = true
        )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C1A17))
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            ProfileHeader(
                username = state.user?.username.orEmpty(),
                elo = state.stats?.rating ?: 1200,
                friends = state.friendsCount,
                gamesPlayed = state.stats?.gamesPlayed ?: 0,
                wins = state.stats?.wins ?: 0,
                losses = state.stats?.losses ?: 0,
                draws = state.stats?.draws ?: 0
            )

            Spacer(modifier = Modifier.height(20.dp))

            TabRow(
                selectedTabIndex = state.historyType.ordinal,
                containerColor = Color(0xFF262421),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[state.historyType.ordinal]),
                        color = Color(0xFF81B64C)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = state.historyType == HistoryType.ONLINE,
                    onClick = { onHistoryTypeChange(HistoryType.ONLINE) },
                    text = { Text("Đấu Online", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                )
                Tab(
                    selected = state.historyType == HistoryType.LOCAL,
                    onClick = { onHistoryTypeChange(HistoryType.LOCAL) },
                    text = { Text("Đấu Máy/Local", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onRefresh,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFbabfc3)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF312E2B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Làm mới",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Làm mới", fontSize = 13.sp)
                }

                IconButton(
                    onClick = { showSearchModal = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF262421)
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Bộ lọc tìm kiếm",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ActiveFilterTags(
                filterOpponent = state.filterOpponent,
                filterResult = state.filterResult,
                onRemoveOpponent = { onFilterOpponentChange("") },
                onRemoveResult = { onFilterResultChange("") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                MatchHistoryCard(
                    history = state.filteredHistory,
                    onReplayClick = { gameItem ->
                        selectedGameForReplay = gameItem
                    },
                    username = username
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showSearchModal) {
        FilterModal(
            filterOpponent = state.filterOpponent,
            filterResult = state.filterResult,
            onFilterOpponentChange = onFilterOpponentChange,
            onFilterResultChange = onFilterResultChange,
            onResetFilters = onResetFilters,
            onDismiss = { showSearchModal = false }
        )
    }

    if (selectedGameForReplay != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedGameForReplay = null },
            sheetState = replaySheetState,
            modifier = Modifier.fillMaxHeight(),
            containerColor = Color(0xFF1C1A17)
        ) {
            ReplayScreen(
                game = selectedGameForReplay!!,
                onCloseReplay = { selectedGameForReplay = null }
            )
        }
    }
}