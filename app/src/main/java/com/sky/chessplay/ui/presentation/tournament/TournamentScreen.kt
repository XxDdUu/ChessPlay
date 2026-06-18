package com.sky.chessplay.ui.presentation.tournament

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sky.chessplay.ui.component.tournament.ActiveFilterTags
import com.sky.chessplay.ui.component.tournament.TournamentFilterDialog
import com.sky.chessplay.ui.component.tournament.TournamentItem
import com.sky.chessplay.ui.component.tournament.TournamentRow
import com.sky.chessplay.ui.component.tournament.TournamentTableHeader
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentScreen(
    viewModel: TournamentViewModel = hiltViewModel(),
    currentUserId: Long?,
    onTournamentClick: (Long) -> Unit,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val todayStr = remember { dateFormatter.format(Date()) }
    var selectedDateStr by remember { mutableStateOf<String?>(todayStr) }
    var showFilterDialog by remember { mutableStateOf(false) }
    val apiDateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val filteredTournaments by remember(uiState.tournaments, searchQuery, selectedDateStr) {
        derivedStateOf {
            uiState.tournaments.filter { tournament ->
                val matchesSearch = searchQuery.isEmpty() ||
                        tournament.name.contains(searchQuery, ignoreCase = true) ||
                        (tournament.description?.contains(searchQuery, ignoreCase = true) == true)

                val matchesDate = if (selectedDateStr == null) {
                    true
                } else {
                    try {
                        val parsedApiDate = apiDateFormatter.parse(tournament.startTime)
                        val formattedApiDateStr = parsedApiDate?.let { dateFormatter.format(it) }

                        formattedApiDateStr == selectedDateStr
                    } catch (e: Exception) {
                        false
                    }
                }

                matchesSearch && matchesDate
            }
        }
    }

    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            showTopBar = true,
            showBottomBar = true,
            title = "Giải đấu"
        )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C1A17)),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tìm kiếm giải đấu...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Xóa", tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true,
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF262421),
                        unfocusedContainerColor = Color(0xFF262421),
                        focusedBorderColor = Color(0xFFFFD54F),
                        unfocusedBorderColor = Color(0xFF312E2B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                IconButton(
                    onClick = { showFilterDialog = true },
                    modifier = Modifier.background(Color(0xFF262421), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Bộ lọc",
                        tint = if (selectedDateStr != null) Color(0xFFFFD54F) else Color.White
                    )
                }
            }

            ActiveFilterTags(
                searchQuery = searchQuery,
                selectedDateStr = selectedDateStr,
                todayStr = todayStr,
                onClearSearch = { searchQuery = "" },
                onClearDate = { selectedDateStr = null }
            )

            AnimatedContent(
                targetState = uiState,
                label = "TournamentStateTransition"
            ) { state ->
                when {
                    state.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    state.error != null -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = state.error ?: "Lỗi không xác định",
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF262421)),
                                    border = BorderStroke(1.dp, Color(0xFF312E2B))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.EmojiEvents,
                                                contentDescription = "Giải đấu",
                                                tint = Color(0xFFFFD54F)
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                text = "Phòng chờ giải đấu",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = Color.White
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = "Khám phá các giải đấu đang diễn ra, tham gia tranh tài hoặc xem bảng xếp hạng.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFFB0B0C3)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = Color(0xFF312E2B))
                            }

                            if (filteredTournaments.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Không có giải đấu nào khớp với bộ lọc của bạn.",
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            } else {
                                items(
                                    items = filteredTournaments,
                                    key = { it.id }
                                ) { tournament ->
                                    TournamentItem(
                                        tournament = tournament,
                                        onJoinClick = { viewModel.joinTournament(it) },
                                        onStandingsClick = onTournamentClick,
                                        onLeaveClick = { viewModel.leaveTournament(it) },
                                        isRegistered = uiState.registeredTournamentIds.contains(tournament.id)
                                    )
                                    HorizontalDivider(color = Color(0xFF312E2B))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        TournamentFilterDialog(
            initialName = searchQuery,
            initialDate = selectedDateStr,
            todayStr = todayStr,
            dateFormatter = dateFormatter,
            onDismiss = { showFilterDialog = false },
            onApply = { name, date ->
                searchQuery = name
                selectedDateStr = date
                showFilterDialog = false
            }
        )
    }
}

@Composable
fun TournamentScreenCompact(
    viewModel: TournamentViewModel = hiltViewModel(),
    onTournamentClick: (Long) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1A17)),
        verticalArrangement = Arrangement.Top
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = uiState.error ?: "Lỗi không xác định",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {

                item {
                    TournamentTableHeader()
                    HorizontalDivider()
                }

                items(
                    items = uiState.tournaments,
                    key = { it.id }
                ) { tournament ->

                    TournamentRow(
                        tournament = tournament,
                        onJoinClick = { viewModel.joinTournament(it) },
                        onStandingsClick = onTournamentClick
                    )

                    HorizontalDivider()
                }
            }
        }
    }
}