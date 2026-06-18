package com.sky.chessplay.ui.component.ai

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ResignSection(
    onResignGame: () -> Unit
) {

    OutlinedButton(
        onClick = onResignGame,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Red
        )
    ) {
        Text("Đầu hàng")
    }
}