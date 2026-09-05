package com.gosleep.app.ui.routine

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gosleep.app.ui.theme.AccentGreen
import com.gosleep.app.ui.theme.NightBackground
import com.gosleep.app.ui.theme.TextDisabled

/**
 * barra di avanzamento "Step X of 6"
 * feedback visivo per step (bottone verde "Done, Next Step"), "Skip routine" de-enfatizzato.
 */
@Composable
fun RoutineFlowScreen(
    viewModel: RoutineFlowViewModel,
    onRoutineCompleted: () -> Unit,
    onRoutineSkipped: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.completed) {
        // "Complete Routine" chiude il loop: naviga verso Relax Mode / dashboard
        androidx.compose.runtime.LaunchedEffect(Unit) { onRoutineCompleted() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NightBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Step ${uiState.stepNumber} of ${uiState.totalSteps}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { uiState.stepNumber.toFloat() / uiState.totalSteps },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = AccentGreen,
        )

        Spacer(Modifier.weight(1f))

        Crossfade(targetState = uiState.currentStep, label = "routine_step") { step ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(step.emoji, style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(16.dp))
                Text(
                    step.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Spacer(Modifier.height(4.dp))
                Text(step.subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { viewModel.onStepDone() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
        ) {
            Text("Done, Next Step  →", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = {
            viewModel.onSkipRoutine()
            onRoutineSkipped()
        }) {
            Text("Skip routine", color = TextDisabled)
        }
    }
}
