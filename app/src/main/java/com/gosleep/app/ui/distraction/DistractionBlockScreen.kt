package com.gosleep.app.ui.distraction

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gosleep.app.ui.theme.*
import android.provider.Settings
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay


/**
 *
 * messaggio antropomorfo "This is stealing your sleep", timer di blocco forzato,
 * tasto viola "Continue to Sleep" e "Tomorrow you will feel" (valutazione preventiva).
 */
@Composable
fun DistractionBlockScreen(
    viewModel: DistractionBlockViewModel,
    bedtimeTargetMinutes: Int,
    onSleepModeActivated: () -> Unit,
    onStayedAwakeAcknowledged: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDndDialog by remember { mutableStateOf(false) }
    var showGoodnightMessage by remember { mutableStateOf(false) }

    if (showDndDialog) {
        AlertDialog(
            onDismissRequest = { showDndDialog = false },
            title = { Text("Do you want to turn on Do Not Disturb?") },
            text = { Text("Block notifications and calls until you wake up so your sleep isn't interrupted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDndDialog = false
                        requestEnableDndBlock(context)
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
            viewModel.onContinueToSleep(bedtimeTargetMinutes, onSleepModeActivated)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NightBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(AccentAmberWarning.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = AccentOrange)
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "This is stealing your sleep",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "You set a bedtime to improve your sleep.\nDistractions will keep you awake.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.weight(1f))

        AnimatedVisibility(visible = !uiState.stayAwakeButtonVisible) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${uiState.secondsRemaining}",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 56.sp),
                    fontWeight = FontWeight.Bold,
                )
                Text("Please wait…", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { showDndDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(GoSleepVioletStart, GoSleepVioletEnd))),
                contentAlignment = Alignment.Center,
            ) {
                Text("🌙  Continue to Sleep", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }

        Spacer(Modifier.height(12.dp))

        AnimatedVisibility(visible = uiState.stayAwakeButtonVisible) {
            TextButton(
                onClick = {
                    viewModel.onChooseToStayAwake()
                    onStayedAwakeAcknowledged()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("I choose to stay awake", color = TextSecondary)
            }
        }

        Spacer(Modifier.height(16.dp))

        Surface(color = CardSurface, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tomorrow you will feel:", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😩", style = MaterialTheme.typography.headlineMedium)
                        Text("If you stay up", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text("vs", modifier = Modifier.align(Alignment.CenterVertically))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚡", style = MaterialTheme.typography.headlineMedium)
                        Text("If you sleep now", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

private fun requestEnableDndBlock(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (notificationManager.isNotificationPolicyAccessGranted) {
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
    } else {
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }
}


