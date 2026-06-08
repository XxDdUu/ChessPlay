package com.sky.chessplay.ui.component.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sky.chessplay.data.remote.dto.response.UserAdminResponse

@Composable
fun AdminUsersTab(
    users: List<UserAdminResponse>,
    isLoading: Boolean,
    onBanClick: (Long) -> Unit,
    onUnbanClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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
                items(users, key = { it.userId }) { user ->
                    UserAdminItem(
                        user = user,
                        onBanClick = { onBanClick(user.userId) },
                        onUnbanClick = { onUnbanClick(user.userId) }
                    )
                }
            }
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
