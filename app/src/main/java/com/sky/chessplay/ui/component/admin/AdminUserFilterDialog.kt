package com.sky.chessplay.ui.component.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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

@Composable
fun AdminUserFilterDialog(
    initialSearchQuery: String,
    initialBanFilter: AdminUserBanFilter,
    onDismiss: () -> Unit,
    onApply: (String, AdminUserBanFilter) -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialSearchQuery) }
    var banFilter by remember { mutableStateOf(initialBanFilter) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF262421),
        title = { Text("Bộ lọc người dùng", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Tên người dùng") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFFD54F),
                        unfocusedBorderColor = Color(0xFF312E2B),
                        focusedLabelColor = Color(0xFFFFD54F),
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AdminUserBanFilter.values().forEach { filter ->
                        FilterChip(
                            selected = banFilter == filter,
                            onClick = { banFilter = filter },
                            label = {
                                Text(
                                    text = when (filter) {
                                        AdminUserBanFilter.All -> "Tất cả"
                                        AdminUserBanFilter.Banned -> "Bị khóa"
                                        AdminUserBanFilter.NotBanned -> "Không bị khóa"
                                    }
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(searchQuery.trim(), banFilter) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD54F),
                    contentColor = Color.Black
                )
            ) {
                Text("Áp dụng")
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = {
                        searchQuery = ""
                        banFilter = AdminUserBanFilter.All
                    }
                ) {
                    Text("Xóa bộ lọc", color = Color(0xFFFFD54F))
                }

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("Hủy", color = Color.White)
                }
            }
        }
    )
}