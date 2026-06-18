package com.sky.chessplay.ui.component.tournament

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
fun ActiveFilterTags(
    searchQuery: String,
    selectedDateStr: String?,
    todayStr: String,
    onClearSearch: () -> Unit,
    onClearDate: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (searchQuery.isNotEmpty() || selectedDateStr != null) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (searchQuery.isNotEmpty()) {
                InputChip(
                    selected = true,
                    onClick = onClearSearch,
                    label = { Text("Text: $searchQuery") },
                    trailingIcon = { Icon(Icons.Default.Close, "Remove", modifier = Modifier.size(14.dp)) },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = Color(0xFF312E2B),
                        selectedLabelColor = Color.White,
                        selectedTrailingIconColor = Color.LightGray
                    )
                )
            }

            if (selectedDateStr != null) {
                val displayDate = if (selectedDateStr == todayStr) "Today" else selectedDateStr
                InputChip(
                    selected = true,
                    onClick = onClearDate,
                    label = { Text("Date: $displayDate") },
                    trailingIcon = { Icon(Icons.Default.Close, "Remove", modifier = Modifier.size(14.dp)) },
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