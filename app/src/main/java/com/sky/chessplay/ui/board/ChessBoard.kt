package view.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.sky.chessplay.domain.model.chess.File
import com.sky.chessplay.domain.model.chess.Position
import com.sky.chessplay.domain.model.chess.Promotion
import com.sky.chessplay.domain.model.chess.Rank
import com.sky.chessplay.ui.board.decorator.PromotionSelection
import com.sky.chessplay.ui.state.UiState
import model.board.Square
import model.state.GameState

@Composable
fun ChessBoard(
    gameState: GameState,
    uiState: UiState,
    onClick: (Position) -> Unit,
    onDragStart: (Position) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onSquareSizeChanged: (Int) -> Unit,
    applyPromotion: (Promotion) -> Unit,
    cancelPromotion: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val squaresByPosition = Position.entries.associateWith { Square(it, gameState) }
    val isFlipped = uiState.isFlipped

    val ranks =
        if (isFlipped) Rank.entries
        else Rank.entries.reversed()

    val files =
        if (isFlipped) File.entries.reversed()
        else File.entries

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(12.dp, RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E24))
            .border(4.dp, Color(0xFF2D2D37), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { onSquareSizeChanged(it.width / 8) }
        ) {
            for (rank in ranks) {
                val zIndexRow = if (gameState.activePosition?.rank == rank) 1f else 0f
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .zIndex(zIndexRow)
                ) {
                    for (file in files) {
                        val square = squaresByPosition.getValue(
                            Position.fromFileAndRank(file, rank)
                        )
                        val zIndexSquare = if (square.isActive) 1f else 0f
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .zIndex(zIndexSquare)
                        ) {
                            Square(
                                uiState = uiState,
                                square = square,
                                onClick = onClick,
                                onDragStart = onDragStart,
                                onDrag = onDrag,
                                onDragEnd = onDragEnd
                            )
                        }
                    }
                }
            }
        }

        PromotionSelection(
            gameState = gameState,
            uiState = uiState,
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
        uiState = UiState(
            canInteract = { false },
            shouldAnimate = { false }
        ),
        onClick = {},
        onDragStart = {},
        onDrag = {},
        onDragEnd = {},
        applyPromotion = {},
        cancelPromotion = {},
        onSquareSizeChanged = {}
    )
}
