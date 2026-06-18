package com.sky.chessplay.ui.component.tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sky.chessplay.domain.model.tournament.Tournament
import com.sky.chessplay.domain.state.TournamentStatus

@Composable
fun TournamentDetailDialog(
    tournament: Tournament,
    isRegistered: Boolean,
    onDismissRequest: () -> Unit,
    onJoinClick: (Long) -> Unit = {},
    onLeaveClick: (Long) -> Unit = {},
    onStandingsClick: (Long) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = tournament.name)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = tournament.description)
                Text(text = "Số ván đấu: ${tournament.totalRounds}")
                Text(text = "Kiểm soát thời gian: ${tournament.timeControl}")
                Text(text = "Thời gian bắt đầu: ${tournament.startTime}")
                Text(text = "Ban tổ chức: ${tournament.createdByName}")
                TournamentStatusChip(status = tournament.status.name)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (tournament.status == TournamentStatus.REGISTERING) {

                        if (isRegistered) {
                            OutlinedButton(
                                onClick = {
                                    onLeaveClick(tournament.id)
                                    onDismissRequest()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Rời giải")
                            }
                        } else {
                            Button(
                                onClick = {
                                    onJoinClick(tournament.id)
                                    onDismissRequest()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF22C55E)
                                )
                            ) {
                                Text("Tham gia")
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            onStandingsClick(tournament.id)
                            onDismissRequest()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Bảng xếp hạng")
                    }
                }
            }
        },
        confirmButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text(text = "Đóng")
            }
        }
    )
}
