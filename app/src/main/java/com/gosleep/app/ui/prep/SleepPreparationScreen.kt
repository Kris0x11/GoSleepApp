package com.gosleep.app.ui.prep

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gosleep.app.ui.theme.*
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ExperimentalMaterial3Api

/**
 * Ricalca la schermata "Sleep Preparation": cerchio con countdown, orario target
 * modificabile (matita), lista dei promemoria (30 min / 10 min / Bedtime!).
 */
@Composable
fun SleepPreparationScreen(
    viewModel: SleepPreparationViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        BedtimeEditDialog(
            currentMinutes = uiState.bedtimeMinutes,
            onDismiss = { showEditDialog = false },
            onConfirm = { minutes ->
                viewModel.onBedtimeChanged(minutes)
                showEditDialog = false
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NightBackground, GoSleepVioletStart.copy(alpha = 0.25f))))
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(Modifier.width(4.dp))
            Text("Sleep Preparation", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(32.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(GoSleepVioletEnd.copy(alpha = 0.6f), GoSleepVioletStart.copy(alpha = 0.9f)))),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text(uiState.countdownLabel, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("until bedtime", style = MaterialTheme.typography.bodyMedium, color = TextPrimary.copy(alpha = 0.8f))
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("Target Bedtime", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(formatMinutes(uiState.bedtimeMinutes), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = GoSleepVioletStart)
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GoSleepVioletStart.copy(alpha = 0.25f)),
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit bedtime", tint = GoSleepVioletStart, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        ReminderRow(title = "30 min warning", subtitle = "Time to start winding down")
        Spacer(Modifier.height(10.dp))
        ReminderRow(title = "10 min warning", subtitle = "Begin your bedtime routine")
        Spacer(Modifier.height(10.dp))
        ReminderRow(title = "Bedtime!", subtitle = "It's time to sleep")
    }
}

@Composable
private fun ReminderRow(title: String, subtitle: String) {
    Surface(color = CardSurface, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CardSurfaceElevated),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Notifications, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BedtimeEditDialog(
    currentMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = currentMinutes / 60,
        initialMinute = currentMinutes % 60,
        is24Hour = true,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Target Bedtime") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // TimePicker con quadrante (dial). Se preferisci solo input numerico
                // sostituisci con TimeInput(state = timePickerState)
                TimePicker(state = timePickerState)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(timePickerState.hour * 60 + timePickerState.minute)
                },
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

private fun formatMinutes(totalMinutes: Int): String {
    val hour = totalMinutes / 60
    val minute = totalMinutes % 60
    return String.format("%02d:%02d", hour, minute)
}
