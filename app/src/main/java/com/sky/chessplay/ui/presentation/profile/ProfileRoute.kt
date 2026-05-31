package com.sky.chessplay.ui.presentation.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.ui.presentation.auth.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val state by viewModel.state.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    if (authState !is AuthState.Authenticated) {
        LaunchedEffect(Unit) {
            navController.navigate("authentication")
        }
        return
    }

    ProfileScreen(
        state = state,
        username = (authState as AuthState.Authenticated).user.username,
        onRefresh = { viewModel.loadProfile() },
        onViewFriends = { navController.navigate("community-friend") },
        navController = navController,
        onFilterOpponentChange = viewModel::onFilterOpponentChanged,
        onFilterResultChange = viewModel::onFilterResultChanged,
        onResetFilters = viewModel::resetFilters
    )
}
