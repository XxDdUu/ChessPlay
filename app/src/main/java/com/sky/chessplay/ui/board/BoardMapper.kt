package com.sky.chessplay.ui.board

import androidx.compose.ui.geometry.Offset
import com.sky.chessplay.domain.model.Position

object BoardMapper {

    fun offsetToPosition(offset: Offset, cellSize: Float): Position? {
        val file = (offset.x / cellSize).toInt() + 1
        val rank = 8 - (offset.y / cellSize).toInt()

        return Position.fromFileAndRank(file, rank)
    }

    fun positionToOffset(position: Position, cellSize: Float): Offset {
        val x = (position.file.number - 1) * cellSize
        val y = (8 - position.rank.number) * cellSize
        return Offset(x, y)
    }
}