package com.sky.chessplay.ui.component.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val resultOptions = listOf("ALL", "WIN", "LOSS", "DRAW")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanel(
    modifier: Modifier = Modifier,
    filterOpponent: String,
    filterResult: String,
    onFilterOpponentChange: (String) -> Unit,
    onFilterResultChange: (String) -> Unit,
    onResetFilters: () -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF262421)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Tìm kiếm ván đấu",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = filterOpponent,
                onValueChange = onFilterOpponentChange,
                label = {
                    Text("Đối thủ")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded.value,
                onExpandedChange = { expanded.value = it }
            ) {
                OutlinedTextField(
                    value = filterResult,
                    onValueChange = { },
                    readOnly = true,
                    label = {
                        Text("Kết quả")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                androidx.compose.material3.DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    resultOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onFilterResultChange(option)
                                expanded.value = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            FilledTonalButton(
                onClick = onResetFilters,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Thiết lập lại")
            }
        }
    }
}
