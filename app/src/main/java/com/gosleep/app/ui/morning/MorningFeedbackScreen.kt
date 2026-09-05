package com.gosleep.app.ui.morning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gosleep.app.ui.theme.*

/**
 * Ricalca la schermata finale: "Good Morning!", card motivazionale ("Keep Trying"),
 * metriche Sleep Score / Streak, "Your Plant Grew! +10% Growth".
 */
@Composable
fun MorningFeedbackScreen(
    viewModel: MorningFeedbackViewModel,
    usedRelaxOrBrainDump: Boolean,
    onDone: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.onWakeUp(usedRelaxOrBrainDump) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NightBackground)
            .padding(24.dp),
    ) {
        Text("☀️", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(8.dp))
        Text("Good Morning!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
            return@Column
        }

        Surface(
            color = AccentOrange.copy(alpha = 0.25f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(uiState.motivationalLabel, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Every night is a new opportunity", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(label = "Sleep Score", value = "${uiState.sleepScore}", suffix = "out of 100", modifier = Modifier.weight(1f))
            MetricCard(label = "Streak", value = "${uiState.streakDays}", suffix = "days in a row", modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        if (uiState.plantJustGrew) {
            Surface(color = AccentGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🌱", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Your Plant Grew!", fontWeight = FontWeight.Bold)
                        Text("+10% Growth · ${uiState.plantGrowthPercent}% total", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(onClick = onDone, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Back to Dashboard", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, suffix: String, modifier: Modifier = Modifier) {
    Surface(color = CardSurface, shape = RoundedCornerShape(16.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(suffix, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
