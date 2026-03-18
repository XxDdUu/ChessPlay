package com.sky.chessplay.ui.component.home

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeMenuButton(text: String, onClick: () -> Unit, width: Int, height: Int) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(width.dp)
            .height(height.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}

