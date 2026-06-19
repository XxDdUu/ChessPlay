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
        isRefreshing = viewModel.isRefreshing,
        errorMessage = viewModel.errorMessage,
        searchResults = viewModel.searchResults,
        isSearching = viewModel.isSearching,
        onEvent = viewModel::onEvent,
        currentUserId = userId,
        navController = navController
    )
}