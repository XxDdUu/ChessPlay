package view.board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.sky.chessplay.domain.model.File
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.model.Promotion
import com.sky.chessplay.domain.model.Rank
import com.sky.chessplay.ui.board.decorator.PromotionSelection
import model.board.Square
import model.service.DefaultChessUiService
import model.state.GameState

@Composable
fun ChessBoard(
    gameState: GameState,
    onClick: (Position) -> Unit,
    onDragStart: (Position) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    applyPromotion: (Promotion) -> Unit,
    cancelPromotion: () -> Unit,
) {
    val squaresByPosition = Position.entries.associateWith { Square(it, gameState) }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .onSizeChanged { DefaultChessUiService.onSquareSizeChanged(it.width / 8) }
    ) {
        Column {
            for (rank in Rank.entries.reversed()) {
                val zIndexRow = if (gameState.activePosition?.rank == rank) 1f else 0f

                Row(modifier = Modifier.weight(1f).zIndex(zIndexRow)) {
                    for (file in File.entries) {
                        val square = squaresByPosition.getValue(Position.fromFileAndRank(file, rank))
                        val zIndexSquare = if (square.isActive) 1f else 0f

                        Box(modifier = Modifier.weight(1f).zIndex(zIndexSquare)) {
                            Square(
                                square = square,
                                onClick = onClick,
                                onDragStart = onDragStart,
                                onDrag = onDrag,
                                onDragEnd = onDragEnd,
                            )
                        }
                    }
                }
            }
        }
        PromotionSelection(
            gameState = gameState,
            applyPromotion = applyPromotion,
            cancelPromotion = cancelPromotion,
        )
    }
}
@Composable
@Preview(showBackground = true)
fun PreviewChessBoard() {
    ChessBoard(
        gameState = GameState(),
        onClick = {},
        onDragStart = {},
        onDrag = {},
        onDragEnd = {},
        applyPromotion = {},
    ) { }
}
