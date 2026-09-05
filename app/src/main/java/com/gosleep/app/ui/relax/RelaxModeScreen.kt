package com.gosleep.app.ui.relax

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gosleep.app.ui.theme.GoSleepVioletStart
import com.gosleep.app.ui.theme.NightBackground
import com.gosleep.app.ui.theme.TextSecondary
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.gosleep.app.ui.theme.TextPrimary
import kotlinx.coroutines.delay
/**
 * Low-interaction UI: nessun bottone visibile a parte i due minimi controlli.
 * Il cerchio si espande su "Inhale" e si contrae su "Exhale" (mapping isomorfico
 * con il movimento della cassa toracica, come descritto nel documento).
 */
@Composable
fun RelaxModeScreen(
    viewModel: RelaxModeViewModel,
    onGoingToSleepNow: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDndDialog by remember { mutableStateOf(false) }
    var showGoodnightMessage by remember { mutableStateOf(false) }

    val circleSize by animateDpAsState(
        targetValue = if (uiState.breathPhase == BreathPhase.INHALE) 220.dp else 140.dp,
        animationSpec = tween(durationMillis = RelaxModeViewModel.INHALE_MILLIS.toInt()),
        label = "breath_circle",
    )

    if (showDndDialog) {
        AlertDialog(
            onDismissRequest = { showDndDialog = false },
            title = { Text("Do you want to turn on Do Not Disturb?") },
            text = { Text("Block notifications and calls until you wake up so your sleep isn't interrupted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDndDialog = false
                        requestEnableDndRelax(context)
                        showGoodnightMessage = true
                    },
                ) { Text("Yes, turn it on") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDndDialog = false
                        showGoodnightMessage = true
                    },
                ) { Text("No, thanks") }
            },
        )
    }

    if (showGoodnightMessage) {
        LaunchedEffect(Unit) {
            delay(3000)
            onGoingToSleepNow()
            (context as? Activity)?.moveTaskToBack(true)
        }
        Box(
            modifier = Modifier.fillMaxSize().background(NightBackground),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌙", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(24.dp))
                Text("Good night!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text("ZzZzZzZ", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            }
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .background(GoSleepVioletStart.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (uiState.breathPhase == BreathPhase.INHALE) "Inhale" else "Exhale",
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Spacer(Modifier.height(48.dp))
            Text("Hold", style = MaterialTheme.typography.bodyMedium)
            Text("Follow the circle", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

            Spacer(Modifier.height(32.dp))
            TextButton(onClick = { viewModel.onToggleWhiteNoise() }) {
                Text(if (uiState.whiteNoiseOn) "White Noise On" else "White Noise Off", color = TextSecondary)
            }
            TextButton(onClick = { showDndDialog = true }) {
                Text("🌙  I'm going to sleep now", color = TextSecondary)
            }
        }
    }
}

private fun requestEnableDndRelax(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (notificationManager.isNotificationPolicyAccessGranted) {
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
    } else {
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }
}
