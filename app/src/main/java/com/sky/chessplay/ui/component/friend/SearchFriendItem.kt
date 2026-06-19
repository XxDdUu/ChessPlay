package com.sky.chessplay.ui.component.friend

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.data.remote.dto.response.UserSearchResponse
import com.sky.chessplay.domain.state.FriendshipStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFriendItem(
    user: UserSearchResponse,
    onSendRequest: () -> Unit,
    onAcceptRequest: () -> Unit
) {
    var localStatus by remember { mutableStateOf(user.friendshipStatus) }

    LaunchedEffect(user.friendshipStatus) {
        localStatus = user.friendshipStatus
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF262421)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF81B64C)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.username.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip(
                            containerColor = Color(0xFF32312F),
                            contentColor = Color.White
                        ) {
                            Text(text = user.username, fontSize = 14.sp)
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    Text(
                        text = user.username,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "🛡️ Rating: ${user.rating}",
                    color = Color(0xFFA8A6A4),
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier.width(110.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Crossfade(targetState = localStatus, label = "friendship_status_anim") { status ->
                    when (status) {
                        FriendshipStatus.NONE -> {
                            Button(
                                onClick = {
                                    localStatus = FriendshipStatus.PENDING_SENT
                                    onSendRequest()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF81B64C)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth() // Fills the explicit 110.dp wrapper completely
                            ) {
                                Text(
                                    text = "Kết bạn",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        FriendshipStatus.PENDING_SENT -> {
                            AssistChip(
                                onClick = {},
                                enabled = false,
                                colors = AssistChipDefaults.assistChipColors(
                                    disabledLabelColor = Color.LightGray,
                                    disabledContainerColor = Color(0xFF32312F)
                                ),
                                border = null,
                                label = {
                                    Text(
                                        text = "Đang chờ",
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        FriendshipStatus.PENDING_RECEIVED -> {
                            Button(
                                onClick = {
                                    localStatus = FriendshipStatus.ACCEPTED
                                    onAcceptRequest()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3B82F6)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Chấp nhận",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        FriendshipStatus.ACCEPTED -> {
                            Text(
                                text = "✓ Bạn bè",
                                color = Color(0xFF81B64C),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center, // Centers text within the bounds
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}