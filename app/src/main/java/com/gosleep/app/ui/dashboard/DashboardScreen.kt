package com.gosleep.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gosleep.app.data.repository.SleepQuality
import com.gosleep.app.ui.theme.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
/**
 * Dashboard fedele al mock di riferimento: saluto dinamico, card "Bedtime Tonight"
 * con azione "Prepare", metriche "Last Night" / "Streak" affiancate, "Energy Level"
 * con anello circolare, e "Your Sleep Plant" con stadio + barra di crescita.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenBrainDump: () -> Unit,
    onOpenNightMode: () -> Unit,
    onOpenSleepPreparation: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NightBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        // Header: saluto + icona luna
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(uiState.greeting, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Ready for better sleep tonight?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            Icon(Icons.Filled.NightsStay, contentDescription = null, tint = GoSleepVioletStart)
        }

        Spacer(Modifier.height(16.dp))

        // Bedtime Tonight — card in evidenza  e azione "Prepare"
        Surface(
            onClick = onOpenSleepPreparation,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            color = androidx.compose.ui.graphics.Color.Transparent,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(GoSleepVioletStart, GoSleepVioletEnd))),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Bedtime Tonight", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            formatMinutes(uiState.bedtimeMinutes),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Prepare", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextPrimary)
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Last Night / Streak affiancate
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Surface(color = CardSurface, shape = RoundedCornerShape(16.dp), modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🌙 Last Night", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        if (uiState.hasLastNightScore) "${uiState.lastSleepScore}" else "--",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Sleep Score", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Surface(color = CardSurface, shape = RoundedCornerShape(16.dp), modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🔥 Streak", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "${uiState.streakDays}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Days in a row", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Energy Level — emoji + etichetta + anello circolare percentuale
        Surface(color = CardSurface, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Energy Level", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(uiState.energyEmoji, style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            uiState.energyLabel,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AccentOrange,
                        )
                    }
                }
                EnergyRing(percent = uiState.energyPercent)
            }
        }

        Spacer(Modifier.height(14.dp))

        // Your Sleep Plant — stadio + barra di crescita + messaggio motivazionale
        Surface(color = AccentGreen.copy(alpha = 0.12f), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Your Sleep Plant", style = MaterialTheme.typography.bodyMedium, color = AccentGreen)
                        Text(uiState.plantStageLabel, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Text(plantEmoji(uiState.plantGrowthPercent), style = MaterialTheme.typography.headlineLarge)
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Growth", style = MaterialTheme.typography.bodyMedium, color = AccentGreen)
                    Text("${uiState.plantGrowthPercent}%", style = MaterialTheme.typography.bodyMedium, color = AccentGreen)
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { uiState.plantGrowthPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AccentGreen,
                    trackColor = CardSurfaceElevated,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "🌱 Keep following your routine to help your plant grow!",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        NavigationCard(emoji = "📝", title = "Brain Dump", subtitle = "Clear your mind", onClick = onOpenBrainDump)
        Spacer(Modifier.height(10.dp))
        NavigationCard(emoji = "🌙", title = "Night Mode", subtitle = "Start winding down", onClick = onOpenNightMode)

        Spacer(Modifier.height(20.dp))

        // DEBUG
        Surface(color = CardSurface, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "DEBUG — Simula settimana",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { viewModel.simulateWeek(SleepQuality.POOR) },
                        modifier = Modifier.weight(1f),
                    ) { Text("Poco") }
                    OutlinedButton(
                        onClick = { viewModel.simulateWeek(SleepQuality.NORMAL) },
                        modifier = Modifier.weight(1f),
                    ) { Text("Normale") }
                    OutlinedButton(
                        onClick = { viewModel.simulateWeek(SleepQuality.GOOD) },
                        modifier = Modifier.weight(1f),
                    ) { Text("Bene") }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.resetDebugData() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text("Reset") }
            }

        }
            }

    }


@Composable
private fun EnergyRing(percent: Int) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier.size(64.dp),
            color = AccentOrange,
            trackColor = CardSurfaceElevated,
            strokeWidth = 6.dp,
        )
        Text("$percent%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun NavigationCard(emoji: String, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = CardSurface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CardSurfaceElevated),
                contentAlignment = Alignment.Center,
            ) { Text(emoji) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private fun formatMinutes(totalMinutes: Int): String {
    val hour = totalMinutes / 60
    val minute = totalMinutes % 60
    return String.format("%02d:%02d", hour, minute)
}

private fun plantEmoji(growthPercent: Int): String = when {
    growthPercent >= 80 -> "🌸"
    growthPercent >= 40 -> "🌿"
    growthPercent > 0 -> "🌱"
    else -> "🌰"
}
