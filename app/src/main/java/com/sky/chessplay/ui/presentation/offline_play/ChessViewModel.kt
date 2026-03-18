package com.sky.chessplay.ui.presentation.offline_play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.model.Promotion
import dagger.hilt.android.lifecycle.HiltViewModel
import model.service.ChessUiService
import javax.inject.Inject

@HiltViewModel
class ChessViewModel @Inject constructor(
    private val uiService: ChessUiService
) : ViewModel() {

    var gameState by mutableStateOf(uiService.gameState)
        private set

    fun onClick(position: Position) {
        uiService.onClick(position)
        gameState = uiService.gameState
    }

    fun onDragStart(position: Position) {
        uiService.onDragStart(position)
        gameState = uiService.gameState
    }

    fun onDrag(offset: Offset) {
        uiService.onDrag(offset)
        gameState = uiService.gameState
    }

    fun onDragEnd() {
        uiService.onDragEnd()
        gameState = uiService.gameState
    }

    fun applyPromotion(promotion: Promotion) {
        uiService.applyPromotion(promotion)
        gameState = uiService.gameState
    }

    fun cancelPromotion() {
        uiService.cancelPromotion()
        gameState = uiService.gameState
    }

    fun onSquareSizeChanged(size: Int) {
        uiService.onSquareSizeChanged(size)
        gameState = uiService.gameState
    }
}
