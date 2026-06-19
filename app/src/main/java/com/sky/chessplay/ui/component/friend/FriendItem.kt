package com.sky.chessplay.ui.component.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.data.remote.dto.response.FriendResponse

@Composable
fun FriendItem(
    friend: FriendResponse,
    onChallengeClick: (() -> Unit)? = null,
    onRemoveFriend: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {

    var showRemoveDialog by remember {
        mutableStateOf(false)
    }

    if (showRemoveDialog) {

        AlertDialog(

            onDismissRequest = {
                showRemoveDialog = false
            },

            title = {
                Text("Xóa bạn")
            },

            text = {
                Text(
                    "Bạn có chắc chắn muốn xóa ${friend.username} khỏi danh sách bạn bè?"
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        showRemoveDialog = false

                        onRemoveFriend?.invoke()
                    }
                ) {

                    Text(
                        "Xóa",
                        color = Color.Red
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showRemoveDialog = false
                    }
                ) {

                    Text("Hủy")
                }
            }
        )
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1F26)
        ),
        shape = RoundedCornerShape(16.dp),
        onClick = { onClick?.invoke() },
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = friend.username.first().uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = friend.username,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (friend.status == "ONLINE")
                                    Color.Green
                                else
                                    Color.Gray
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (friend.status == "ONLINE") "Trực tuyến" else "Ngoại tuyến",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = "⭐ ${friend.rating}",
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (onChallengeClick != null) {

                        TextButton(
                            onClick = onChallengeClick
                        ) {

                            Text("Thách đấu")
                        }
                    }

                    if (onRemoveFriend != null) {

                        IconButton(

                            onClick = {
                                showRemoveDialog = true
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Friend",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}