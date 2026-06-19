package com.sky.chessplay.ui.presentation.community

import FriendEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun FriendRoute(
    viewModel: FriendViewModel = hiltViewModel(),
    userId: Long,
    navController: NavHostController
) {
    LaunchedEffect(Unit) {
        viewModel.onEvent(FriendEvent.LoadFriends(userId))
        viewModel.onEvent(FriendEvent.LoadPendingRequests(userId))
    }

    FriendScreen(
        friends = viewModel.friendsList,
        pendingRequests = viewModel.pendingRequestsList,
        searchResults = viewModel.searchResultList,
        isSearching = viewModel.isSearching,
        isRefreshing = viewModel.isRefreshing,
        errorMessage = viewModel.errorMessage,
        leaderboard = viewModel.leaderboardList,
        selectedPlayerStats = viewModel.selectedPlayerStats,
        isPlayerStatsLoading = viewModel.isPlayerStatsLoading,
        onPlayerClick = viewModel::loadPlayerStats,
        onDismissPlayerStats = viewModel::clearPlayerStats,
        onEvent = viewModel::onEvent,
        currentUserId = userId,
        navController = navController
    )
}