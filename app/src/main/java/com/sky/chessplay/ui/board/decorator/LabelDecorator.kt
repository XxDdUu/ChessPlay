package view.board.decorator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sky.chessplay.domain.model.File
import com.sky.chessplay.domain.model.Rank
import model.board.Square

object LabelDecorator : SquareDecorator {

    @Composable
    override fun decorate(square: Square) {

        Box(Modifier.fillMaxSize()) {

            // Rank (1–8)
            if (square.position.file == File.a) {
                Text(
                    text = square.position.rank.number.toString(),
                    color = square.color,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                )
            }

            // File (a–h)
            if (square.position.rank == Rank.r1) {
                Text(
                    text = square.position.file.name,
                    color = square.color,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                )
            }
        }
    }
}

