package com.sky.chessplay.domain.state

import com.sky.chessplay.data.remote.dto.response.AdminDashboardResponse
import com.sky.chessplay.data.remote.dto.response.UserAdminResponse
import com.sky.chessplay.data.remote.dto.response.TournamentResponse as Tournament

data class AdminUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val dashboardStats: AdminDashboardResponse? = null,
    val users: List<UserAdminResponse> = emptyList(),
    val selectedTab: AdminTab = AdminTab.Dashboard,
    val tournaments: List<Tournament> = emptyList()
)

enum class AdminTab {
    Dashboard, Users, Tournaments
}
