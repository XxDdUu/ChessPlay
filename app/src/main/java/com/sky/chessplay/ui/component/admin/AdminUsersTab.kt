package com.sky.chessplay.ui.component.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sky.chessplay.data.remote.dto.response.UserAdminResponse

enum class AdminUserBanFilter {
    All,
    Banned,
    NotBanned
}

@Composable
fun AdminUsersTab(
    users: List<UserAdminResponse>,
    isLoading: Boolean,
    searchQuery: String,
    banFilter: AdminUserBanFilter,
    showFilterDialog: Boolean,
    onDismissFilterDialog: () -> Unit,
    onApplyFilters: (String, AdminUserBanFilter) -> Unit,
    onClearSearch: () -> Unit,
    onClearBanFilter: () -> Unit,
    onBanClick: (Long) -> Unit,
    onUnbanClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredUsers = remember(users, searchQuery, banFilter) {
        users.filter { user ->
            val matchesName = searchQuery.isBlank() ||
                    user.username.contains(searchQuery.trim(), ignoreCase = true)
            val matchesBanStatus = when (banFilter) {
                AdminUserBanFilter.All -> true
                AdminUserBanFilter.Banned -> user.isBanned
                AdminUserBanFilter.NotBanned -> !user.isBanned
            }

            matchesName && matchesBanStatus
        }
    }

    Column(
        modifier = modifier) {
        AdminUserActiveFilterTags(
            searchQuery = searchQuery,
            banFilter = banFilter,
            onClearSearch = onClearSearch,
            onClearBanFilter = onClearBanFilter
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredUsers, key = { it.userId }) { user ->
                    UserAdminItem(
                        user = user,
                        onBanClick = { onBanClick(user.userId) },
                        onUnbanClick = { onUnbanClick(user.userId) }
                    )
                }
            }
        }

        if (showFilterDialog) {
            AdminUserFilterDialog(
                initialSearchQuery = searchQuery,
                initialBanFilter = banFilter,
                onDismiss = onDismissFilterDialog,
                onApply = onApplyFilters
            )
        }
    }
}

@Composable
private fun UserAdminItem(
    user: UserAdminResponse,
    onBanClick: () -> Unit,
    onUnbanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Badge {
                        Text(user.role)
                    }
                    if (user.isBanned) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error
                        ) {
                            Text("BANNED")
                        }
                    }
                }
            }

            // Action Buttons
            if (user.isBanned) {
                IconButton(onClick = onUnbanClick) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Unban user",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                IconButton(onClick = onBanClick) {
                    Icon(
                        Icons.Default.Block,
                        contentDescription = "Ban user",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
