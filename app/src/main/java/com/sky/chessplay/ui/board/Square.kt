package view.board

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.sky.chessplay.domain.model.Position
import model.board.Square
import view.board.decorator.ActivePieceDecorator
import view.board.decorator.BackgroundDecorator
import view.board.decorator.CaptureDecorator
import view.board.decorator.LabelDecorator
import view.board.decorator.LegalMoveDecorator

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Square(
    square: Square,
    onClick: (Position) -> Unit,
    onDragStart: (Position) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable() { onClick(square.position) }
    ) {
        squareDecorators.forEach {
            it.decorate(square)
        }
        Piece(
            square = square,
            onDragStart = onDragStart,
            onDrag = onDrag,
            onDragEnd = onDragEnd,
        )
    }
}

private val squareDecorators = arrayOf(
    BackgroundDecorator,
    ActivePieceDecorator,
    LegalMoveDecorator,
    CaptureDecorator,
    LabelDecorator,
)
