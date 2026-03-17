package com.sky.chessplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sky.chessplay.ui.root.ChessPlayRoot
import com.sky.chessplay.ui.theme.ChessPlayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChessPlayTheme {
                ChessPlayRoot()
            }
        }
    }
}