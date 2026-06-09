package com.sky.chessplay.ui.component.admin

import TournamentRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sky.chessplay.ui.component.common.DateTimeField
import com.sky.chessplay.ui.component.tournament.TournamentDatePicker
import com.sky.chessplay.ui.component.tournament.TournamentTimePicker
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTournamentDialog(
    onDismiss: () -> Unit,
    onCreate: (TournamentRequest) -> Unit
) {
    var tournamentName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var totalRounds by remember { mutableStateOf("5") }
    var timeControl by remember { mutableStateOf("10+0") }

    var registrationStart by remember { mutableStateOf<LocalDateTime?>(null) }
    var registrationEnd by remember { mutableStateOf<LocalDateTime?>(null) }
    var startTime by remember { mutableStateOf<LocalDateTime?>(null) }

    var selectedTarget by remember { mutableStateOf<DateTarget?>(null) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create Tournament",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = tournamentName,
                    onValueChange = { tournamentName = it },
                    label = { Text("Tournament Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = totalRounds,
                    onValueChange = { totalRounds = it.filter(Char::isDigit) },
                    label = { Text("Total Rounds") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = timeControl,
                    onValueChange = { timeControl = it },
                    label = { Text("Time Control (10+0)") },
                    modifier = Modifier.fillMaxWidth()
                )

                DateTimeField(
                    label = "Registration Start",
                    value = registrationStart,
                    onClick = {
                        selectedTarget = DateTarget.REGISTRATION_START
                        showDatePicker = true
                    }
                )

                DateTimeField(
                    label = "Registration End",
                    value = registrationEnd,
                    onClick = {
                        selectedTarget = DateTarget.REGISTRATION_END
                        showDatePicker = true
                    }
                )

                // FIX 1: Fixed the assigned DateTarget here
                DateTimeField(
                    label = "Tournament Start",
                    value = startTime,
                    onClick = {
                        selectedTarget = DateTarget.TOURNAMENT_START
                        showDatePicker = true
                    }
                )
            }

            // NOTE: Ensure your Date Picker logic updates `selectedDateMillis`
            // before setting `showDatePicker = false` and `showTimePicker = true`.
            if (showDatePicker) {
                // Example wrapper assuming you're using a standard Material 3 DatePickerDialog:
                TournamentDatePicker(
                    onDateSelected = { millis ->
                        selectedDateMillis = millis
                        showDatePicker = false
                        showTimePicker = true
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            if (showTimePicker) {
                TournamentTimePicker(
                    onConfirm = { hour, minute ->
                        // FIX 2: Added a safe guard check to prevent crashes if state breaks
                        val millis = selectedDateMillis
                        if (millis != null) {
                            val date = Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()

                            val dateTime = LocalDateTime.of(date, LocalTime.of(hour, minute))

                            when (selectedTarget) {
                                DateTarget.REGISTRATION_START -> registrationStart = dateTime
                                DateTarget.REGISTRATION_END -> registrationEnd = dateTime
                                DateTarget.TOURNAMENT_START -> startTime = dateTime
                                null -> {}
                            }
                        }
                        showTimePicker = false
                    },
                    onDismiss = {
                        showTimePicker = false
                    }
                )
            }
        },
        confirmButton = {
            Button(
                enabled = tournamentName.isNotBlank(),
                onClick = {
                    onCreate(
                        TournamentRequest(
                            tournamentName = tournamentName,
                            description = description,
                            totalRounds = totalRounds.toIntOrNull() ?: 1,
                            timeControl = timeControl,

                            registrationStart = registrationStart?.toInstant(ZoneOffset.UTC)?.toString() ?: "",
                            registrationEnd = registrationEnd?.toInstant(ZoneOffset.UTC)?.toString() ?: "",
                            startTime = startTime?.toInstant(ZoneOffset.UTC)?.toString() ?: ""
                        )
                    )
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
enum class DateTarget {
    REGISTRATION_START,
    REGISTRATION_END,
    TOURNAMENT_START
}