package com.sky.chessplay.ui.component.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val resultOptions = listOf("ALL", "WIN", "LOSS", "DRAW")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterModal(
    filterOpponent: String,
    filterResult: String,
    onFilterOpponentChange: (String) -> Unit,
    onFilterResultChange: (String) -> Unit,
    onResetFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Áp dụng")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onResetFilters()
                    onDismiss()
                }
            ) {
                Text("Xóa bộ lọc", color = Color.Gray)
            }
        },
        title = {
            Text("Tìm kiếm ván đấu", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = filterOpponent,
                    onValueChange = onFilterOpponentChange,
                    label = { Text("Đối thủ") },
                    placeholder = { Text("Nhập tên đối thủ...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = when (filterResult) {
                            "ALL", "" -> "Tất cả"
                            "WIN" -> "Thắng"
                            "LOSS" -> "Thua"
                            "DRAW" -> "Hòa"
                            else -> filterResult
                        },
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Kết quả") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        resultOptions.forEach { option ->
                            val label = when (option) {
                                "ALL" -> "Tất cả"
                                "WIN" -> "Thắng"
                                "LOSS" -> "Thua"
                                "DRAW" -> "Hòa"
                                else -> option
                            }
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    onFilterResultChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF262421),
        shape = RoundedCornerShape(16.dp)
    )
}
