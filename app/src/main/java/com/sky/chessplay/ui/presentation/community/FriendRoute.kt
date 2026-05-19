package com.sky.chessplay.ui.presentation.community

import FriendEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun FriendRoute(
    viewModel: FriendViewModel = hiltViewModel(),
    userId: Long,
    onNavigateToDiscover: () -> Unit,
    navController: NavHostController
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {

        viewModel.onEvent(
            FriendEvent.LoadFriends(userId)
        )
        viewModel.onEvent(
            FriendEvent.LoadPendingRequests(userId)
        )
    }

    FriendScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToDiscover = onNavigateToDiscover,
        navController = navController
    )
}