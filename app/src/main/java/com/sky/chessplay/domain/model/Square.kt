package model.board

import androidx.compose.ui.graphics.Color
import com.sky.chessplay.domain.model.CapturingMove
import com.sky.chessplay.domain.model.NonCapturingMove
import com.sky.chessplay.domain.model.Position
import model.state.GameState

private val lightSquareColor = Color(0xFFEEEED2)
private val darkSquareColor = Color(0xFF769656)

class Square(
    val position: Position,
    val gameState: GameState,
) {
    val isActive = gameState.activePosition == position
    val piece: Piece? = gameState.piecesByPosition[position]

    val isLegalMove = gameState.legalMoves.filterIsInstance<NonCapturingMove>().any { it.to == position }
    val isCapture = gameState.legalMoves.filterIsInstance<CapturingMove>().any { it.to == position }

    val isLightSquare = (position.ordinal + position.file.ordinal % 2) % 2 == 1
    val isDarkSquare = (position.ordinal + position.file.ordinal % 2) % 2 == 0

    val backgroundColor = if (isDarkSquare) darkSquareColor else lightSquareColor
    val color = if (isLightSquare) darkSquareColor else lightSquareColor
}
