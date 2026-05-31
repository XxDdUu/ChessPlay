package com.sky.chessplay.ui.component.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveFilterTags(
    filterOpponent: String,
    filterResult: String,
    onRemoveOpponent: () -> Unit,
    onRemoveResult: () -> Unit
) {
    if (filterOpponent.isNotEmpty() || filterResult.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()), // Cuộn ngang nếu tag quá dài
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bộ lọc:",
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier.padding(end = 4.dp)
            )

            // Tag của Đối thủ
            if (filterOpponent.isNotEmpty()) {
                InputChip(
                    selected = true,
                    onClick = {},
                    label = { Text("Đối thủ: $filterOpponent", color = Color.White) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Bỏ lọc đối thủ",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onRemoveOpponent() },
                            tint = Color.LightGray
                        )
                    },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = Color(0xFF363431)
                    )
                )
            }

            if (filterResult.isNotEmpty()) {
                InputChip(
                    selected = true,
                    onClick = {},
                    label = { Text("Kết quả: $filterResult", color = Color.White) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Bỏ lọc kết quả",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onRemoveResult() },
                            tint = Color.LightGray
                        )
                    },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = Color(0xFF363431)
                    )
                )
            }
        }
    }
}