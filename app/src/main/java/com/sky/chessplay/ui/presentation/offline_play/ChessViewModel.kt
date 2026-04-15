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
    init {
        uiService.updateOnStateChanges {
            gameState = it
        }
    }
    fun onClick(position: Position) {
        uiService.onClick(position)
    }

    fun onDragStart(position: Position) {
        uiService.onDragStart(position)
    }

    fun onDrag(offset: Offset) {
        uiService.onDrag(offset)
    }

    fun onDragEnd() {
        uiService.onDragEnd()
    }

    fun applyPromotion(promotion: Promotion) {
        uiService.applyPromotion(promotion)
    }

    fun cancelPromotion() {
        uiService.cancelPromotion()
    }

    fun onSquareSizeChanged(size: Int) {
        uiService.onSquareSizeChanged(size)
    }
}
