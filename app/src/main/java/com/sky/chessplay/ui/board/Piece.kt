package view.board

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.ui.graphics.SvgCache
import model.board.Square

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Piece(
    square: Square,
    onDragStart: (Position) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
) {
    val dragOffset = if (square.isActive) square.gameState.uiState.constrainedPieceDragOffset else Offset.Zero
    if (square.piece != null) {
        val context = LocalContext.current
        val drawable = remember(square.piece.asset) {
            SvgCache.get(context, square.piece.asset)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberDrawablePainter(drawable),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(0.7f)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                onDragStart(square.position)
                            },
                            onDrag = { change, dragAmount ->
                                onDrag(dragAmount)
                            },
                            onDragEnd = {
                                onDragEnd()
                            }
                        )
                    }
                    .offset(
                        dragOffset.x.dp,
                        dragOffset.y.dp,
                    ),
            )
        }
    }
}
