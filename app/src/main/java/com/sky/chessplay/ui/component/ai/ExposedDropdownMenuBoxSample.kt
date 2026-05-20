package com.sky.chessplay.ui.component.ai

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sky.chessplay.data.remote.dto.response.AiModelInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxSample(
    aiModels: List<AiModelInfo>,
    selectedModel: String,
    enabled: Boolean,
    onSelectModel: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val selectedName = aiModels
        .find { it.key == selectedModel }
        ?.display
        ?: selectedModel

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) {
                expanded = !expanded
            }
        }
    ) {

        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = {
                Text("AI Model")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {

            aiModels.forEach { model ->

                DropdownMenuItem(
                    text = {
                        model.display?.let { Text(it) }
                    },
                    onClick = {
                        onSelectModel(model.key)
                        expanded = false
                    }
                )
            }
        }
    }
}