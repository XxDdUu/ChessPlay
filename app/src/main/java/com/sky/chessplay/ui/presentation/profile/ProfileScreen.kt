package com.sky.chessplay.ui.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.model.profile.GameHistoryItem
import com.sky.chessplay.ui.component.profile.FilterPanel
import com.sky.chessplay.ui.component.profile.MatchHistoryCard
import com.sky.chessplay.ui.component.profile.ProfileHeader
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig

@Composable
fun ProfileScreen(
    state: ProfileState,
    username: String,
    onRefresh: () -> Unit,
    onViewFriends: () -> Unit,
    navController: NavHostController,
    onReplayClick: (GameHistoryItem) -> Unit,
    onFilterOpponentChange: (String) -> Unit = {},
    onFilterResultChange: (String) -> Unit = {},
    onResetFilters: () -> Unit = {}
) {

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            title = "Chess Play",
            showTopBar = true,
            showBottomBar = false
        )
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C1A17))
                .padding(20.dp)
        ) {

            ProfileHeader(
                username = state.user?.username.orEmpty(),
                elo = state.stats?.rating ?: 1200,
                friends = state.friendsCount,
                gamesPlayed = state.stats?.gamesPlayed ?: 0,
                wins = state.stats?.wins ?: 0,
                losses = state.stats?.losses ?: 0,
                draws = state.stats?.draws ?: 0
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = onRefresh) {
                    Text("Làm mới")
                }

                Button(onClick = onViewFriends) {
                    Text("Bạn bè")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FilterPanel(
                modifier = Modifier.fillMaxWidth(),
                filterOpponent = state.filterOpponent,
                filterResult = state.filterResult,
                onFilterOpponentChange = onFilterOpponentChange,
                onFilterResultChange = onFilterResultChange,
                onResetFilters = onResetFilters
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.weight(1f)
            ) {
                MatchHistoryCard(
                    history = state.filteredHistory,
                    onReplayClick = onReplayClick,
                    username = username
                )
            }
        }
    }
}