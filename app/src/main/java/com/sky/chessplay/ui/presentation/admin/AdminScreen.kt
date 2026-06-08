package com.sky.chessplay.ui.presentation.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.state.AdminTab
import com.sky.chessplay.ui.component.admin.AdminDashboardTab
import com.sky.chessplay.ui.component.admin.AdminTournamentsTab
import com.sky.chessplay.ui.component.admin.AdminUsersTab
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig

@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            showTopBar = true,
            showBottomBar = true,
            title = "\uD83D\uDEE1\uFE0F Admin Panel"
        )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            // Tab Navigation
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                AdminTab.values().forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content
            when (uiState.selectedTab) {
                AdminTab.Dashboard -> {
                    AdminDashboardTab(
                        stats = uiState.dashboardStats,
                        isLoading = uiState.isLoading,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }
                AdminTab.Users -> {
                    AdminUsersTab(
                        users = uiState.users,
                        isLoading = uiState.isLoading,
                        onBanClick = viewModel::banUser,
                        onUnbanClick = viewModel::unbanUser,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    )
                }
                AdminTab.Tournaments -> {
                    AdminTournamentsTab(
                        tournaments = uiState.tournaments,
                        isLoading = uiState.isLoading,
                        onStartClick = viewModel::startTournament,
                        onFinishClick = viewModel::finishTournament,
                        onCancelClick = viewModel::cancelTournament,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            // Error Snackbar
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.error ?: "",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
