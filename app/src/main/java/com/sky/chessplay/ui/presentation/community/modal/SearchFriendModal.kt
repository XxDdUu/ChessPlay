package com.sky.chessplay.ui.presentation.community.modal

import FriendEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.sky.chessplay.data.remote.dto.response.UserSearchResponse
import com.sky.chessplay.ui.component.friend.SearchFriendItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFriendModal(
    currentUserId: Long,
    onDismiss: () -> Unit,
    searchResults: List<UserSearchResponse>,
    isLoading: Boolean = false,
    onEvent: (FriendEvent) -> Unit
) {
    var query by remember { mutableStateOf("") }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(480.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E1C1A),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Tìm kiếm bạn bè",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        if (it.length >= 2) {
                            onEvent(FriendEvent.SearchFriend(currentUserId, it))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF5B041),
                        unfocusedBorderColor = Color(0xFF403D3A),
                        focusedLeadingIconColor = Color(0xFFF5B041),
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search Icon")
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = query.isNotEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear text", tint = Color.Gray)
                            }
                        }
                    },
                    placeholder = {
                        Text("Nhập tên hoặc ID người chơi...")
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = Color(0xFFF5B041), // Gold accent color
                                strokeWidth = 3.5.dp,
                                strokeCap = StrokeCap.Round // Smooth rounded caps
                            )
                        }
                        query.length < 2 -> {
                            Text(
                                text = "Nhập ít nhất 2 ký tự để tìm kiếm",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }

                        searchResults.isEmpty() -> {
                            Text(
                                text = "Không tìm thấy người chơi nào phù hợp",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                itemsIndexed(searchResults) { index, user ->
                                    SearchFriendItem(
                                        onSendRequest = {
                                            onEvent(FriendEvent.SendFriendRequest(currentUserId, user.userId))
                                        },
                                        onAcceptRequest = {
                                            onEvent(FriendEvent.AcceptFriendRequest(currentUserId, user.userId))
                                        },
                                        user = user
                                    )

                                    if (index < searchResults.lastIndex) {
                                        HorizontalDivider(
                                            color = Color(0xFF2A2825),
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}