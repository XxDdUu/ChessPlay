package view.board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.sky.chessplay.domain.model.File
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.model.Promotion
import com.sky.chessplay.domain.model.Rank
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
) {
    val squaresByPosition = Position.entries.associateWith { Square(it, gameState) }
    val isFlipped = uiState.isFlipped

    val ranks = if (isFlipped) Rank.entries else Rank.entries.reversed()
    val files = if (isFlipped) File.entries.reversed() else File.entries

    Box(
        modifier = Modifier
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { onSquareSizeChanged(it.width / 8) }
        ) {

            Column(modifier = Modifier.weight(1f)) {
                for (rank in ranks) {

                    val zIndexRow = if (gameState.activePosition?.rank == rank) 1f else 0f

                    Row(modifier = Modifier.weight(1f)) {

                        Text(
                            text = rank.toString(),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .width(16.dp)
                                .align(Alignment.CenterVertically),
                            textAlign = TextAlign.Center
                        )

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
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {

                Spacer(modifier = Modifier.width(16.dp)) // align với rank label

                for (file in files) {
                    Text(
                        text = file.name.lowercase(),
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
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
        onSquareSizeChanged = {}
    ) { }
}
