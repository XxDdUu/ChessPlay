package com.sky.chessplay.ui.board.decorator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sky.chessplay.domain.model.Promotion
import com.sky.chessplay.domain.model.Rank
import com.sky.chessplay.domain.model.Side
import com.sky.chessplay.ui.graphics.SvgCache
import model.board.Queen
import model.state.GameState

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun PromotionSelection(
    gameState: GameState,
    applyPromotion: (Promotion) -> Unit,
    cancelPromotion: () -> Unit,
) {
    if (gameState.promotionSelection.isNotEmpty()) {
        val position = gameState.promotionSelection.first().to
        val offsetX = ((position.file.ordinal + 0.15f) * gameState.uiState.squareSize).dp
        val offsetY =
            (gameState.uiState.squareSize * if (position.rank == Rank.r8) 0.15f else 5.05f).dp - if (position.rank == Rank.r1) 24.dp else 0.dp
        val sortedPromotions = when (gameState.sideToPlay) {
            Side.WHITE ->
                gameState.promotionSelection
                    .sortedByDescending { it.pieceAfterPromotion.value }

            Side.BLACK ->
                gameState.promotionSelection
                    .sortedBy { it.pieceAfterPromotion.value }
        }

        Column(
            modifier = Modifier.width((gameState.uiState.squareSize * 1.0f).dp).absoluteOffset(offsetX, offsetY)
                .clip(RoundedCornerShape(20)),
        ) {
            if (gameState.sideToPlay == Side.BLACK) {
                CancelPromotion(cancelPromotion = cancelPromotion)
            }
            for (promotion in sortedPromotions) {
                val initialColor = if (promotion.pieceAfterPromotion is Queen) Color.LightGray else Color.White
                var color by remember { mutableStateOf(initialColor) }
                val context = LocalContext.current

                val drawable = remember(promotion.pieceAfterPromotion.asset) {
                    SvgCache.get(context, promotion.pieceAfterPromotion.asset)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(color)
                        .pointerInput(PointerEventType.Enter) { color = Color.LightGray }
                        .pointerInput(PointerEventType.Exit) { color = Color.White }
                        .clickable { applyPromotion(promotion) }
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = rememberDrawablePainter(drawable),
                        contentDescription = null,
                    )
                }
            }
            if (gameState.sideToPlay == Side.WHITE) {
                CancelPromotion(cancelPromotion = cancelPromotion)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun CancelPromotion(cancelPromotion: () -> Unit) {
    var color by remember { mutableStateOf(Color.White) }
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(color)
        .pointerInput(PointerEventType.Enter) { color = Color.LightGray }
        .pointerInput(PointerEventType.Exit) { color = Color.White }
        .clickable { cancelPromotion() }
        .padding(10.dp)
    ) {
        Canvas(modifier = Modifier.align(Alignment.Center).height(14.dp).width(14.dp)) {
            drawPath(
                Path().apply {
                    moveTo(0f, 0f)
                    lineTo(14f, 14f)
                    moveTo(0f, 14f)
                    lineTo(14f, 0f)
                },
                color = Color.DarkGray,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
        }
    }
}
