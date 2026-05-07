package com.sky.chessplay.ui.state

import androidx.compose.ui.geometry.Offset
import model.board.Square

data class UiState(
    val squareSize: Int = 1,
    val pieceDragOffset: Offset = Offset.Zero,
    val pieceMinDragOffset: Offset = Offset.Zero,
    val pieceMaxDragOffset: Offset = Offset.Zero,
    val constrainedPieceDragOffset: Offset = Offset.Zero,
    val canInteract: (Square) -> Boolean = { true },
    val shouldAnimate: (Square) -> Boolean = { true },
    val isFlipped: Boolean = false
)