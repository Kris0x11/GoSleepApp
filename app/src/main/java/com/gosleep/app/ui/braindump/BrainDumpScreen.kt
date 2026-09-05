package com.gosleep.app.ui.braindump

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gosleep.app.ui.theme.*
import android.app.NotificationManager
import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import androidx.compose.runtime.setValue


/**
 * Ricalca la schermata .1C: domanda empatica "What's keeping you up?", icona
 * microfono con registrazione vocale reale (SpeechRecognizer di sistema) + tasto
 * "+", lista appunti, "I'm going to sleep now" / "Skip brain dump" in fondo.
 */
@Composable
fun BrainDumpScreen(
    viewModel: BrainDumpViewModel,
    onFinished: () -> Unit,
    onOpenRelaxMode: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val context = LocalContext.current
    var showDndDialog by remember { mutableStateOf(false) }
    var showGoodnightMessage by remember { mutableStateOf(false) }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val transcript = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            viewModel.onVoiceTranscriptReady(transcript.orEmpty())
        } else {
            viewModel.onVoiceTranscriptReady("")
        }
    }

    fun launchSpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "What's keeping you up?")
        }
        viewModel.onStartVoiceRecording()
        speechLauncher.launch(intent)
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted -> if (granted) launchSpeechRecognizer() }

    if (showDndDialog) {
        AlertDialog(
            onDismissRequest = { showDndDialog = false },
            title = { Text("Attivare Non Disturbare?") },
            text = { Text("Blocca notifiche e chiamate finché non ti svegli, per non farti interrompere il sonno.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDndDialog = false
                        requestEnableDndBrainDump(context)
                        showGoodnightMessage = true
                    },
                ) { Text("Sì, attiva") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDndDialog = false
                        showGoodnightMessage = true
                    },
                ) { Text("No, grazie") }
            },
        )
    }

    if (showGoodnightMessage) {
        LaunchedEffect(Unit) {
            delay(3000)
            onFinished()
            (context as? Activity)?.moveTaskToBack(true)
        }
        Box(
            modifier = Modifier.fillMaxSize().background(NightBackground),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌙", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(24.dp))
                Text("Buonanotte!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text("Che il riposo ti accompagni", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NightBackground)
            .padding(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CardSurfaceElevated),
                contentAlignment = Alignment.Center,
            ) { Text("🧠") }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Brain Dump", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Clear your mind for sleep", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Write down what's on your mind. We'll save it for tomorrow.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = uiState.draftText,
                onValueChange = viewModel::onDraftChanged,
                placeholder = { Text("What's keeping you up?") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (uiState.isRecordingVoice) AccentOrange.copy(alpha = 0.4f) else CardSurfaceElevated),
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "Record voice note")
            }
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { viewModel.onSaveTextNote() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(GoSleepVioletStart, GoSleepVioletEnd))),
            ) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }

        Spacer(Modifier.height(16.dp))

        if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧠", style = MaterialTheme.typography.headlineLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("No thoughts yet. Start writing…", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(notes, key = { it.id }) { note ->
                    Surface(color = CardSurface, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(if (note.isVoiceNote) "🎙️" else "📝")
                            Spacer(Modifier.width(8.dp))
                            Text(note.content, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                            IconButton(onClick = { viewModel.onDeleteNote(note) }) {
                                Icon(Icons.Filled.Close, contentDescription = "Remove note", tint = TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onOpenRelaxMode, modifier = Modifier.fillMaxWidth()) {
            Text("🧘  Want to relax first?")
        }
        TextButton(onClick = { showDndDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("🌙  I'm going to sleep now")
        }
        TextButton(onClick = onFinished, modifier = Modifier.fillMaxWidth()) {
            Text("Skip brain dump", color = TextDisabled)
        }
    }
}

private fun requestEnableDndBrainDump(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (notificationManager.isNotificationPolicyAccessGranted) {
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
    } else {
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }
}
