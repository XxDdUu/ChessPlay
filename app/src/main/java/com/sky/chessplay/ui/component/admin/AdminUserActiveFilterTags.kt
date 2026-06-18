package com.sky.chessplay.ui.component.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AdminUserActiveFilterTags(
    searchQuery: String,
    banFilter: AdminUserBanFilter,
    onClearSearch: () -> Unit,
    onClearBanFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (searchQuery.isNotBlank() || banFilter != AdminUserBanFilter.All) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (searchQuery.isNotBlank()) {
                InputChip(
                    selected = true,
                    onClick = onClearSearch,
                    label = { Text("Tên: ${searchQuery.trim()}") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Xóa bộ lọc tên",
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = Color(0xFF312E2B),
                        selectedLabelColor = Color.White,
                        selectedTrailingIconColor = Color.LightGray
                    )
                )
            }

            if (banFilter != AdminUserBanFilter.All) {
                InputChip(
                    selected = true,
                    onClick = onClearBanFilter,
                    label = {
                        Text(
                            text = when (banFilter) {
                                AdminUserBanFilter.Banned -> "Trạng thái: Bị khóa"
                                AdminUserBanFilter.NotBanned -> "Trạng thái: Không bị khóa"
                                AdminUserBanFilter.All -> ""
                            }
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Xóa bộ lọc trạng thái",
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = Color(0xFF312E2B),
                        selectedLabelColor = Color(0xFFFFD54F),
                        selectedTrailingIconColor = Color(0xFFFFD54F)
                    )
                )
            }
        }
    }
}