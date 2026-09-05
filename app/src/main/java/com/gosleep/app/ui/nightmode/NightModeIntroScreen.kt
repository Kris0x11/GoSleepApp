package com.gosleep.app.ui.nightmode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gosleep.app.ui.theme.*

/**
 * Ricalca la schermata "It's time to sleep / Let's guide you to bed", punto
 * d'ingresso a Night Mode dalla dashboard, prima della Routine Flow vera e propria.
 */
@Composable
fun NightModeIntroScreen(onStartSleepRoutine: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NightBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Filled.NightsStay,
            contentDescription = null,
            tint = GoSleepVioletStart,
            modifier = Modifier.size(72.dp),
        )
        Spacer(Modifier.height(24.dp))
        Text("It's time to sleep", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Let's guide you to bed", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onStartSleepRoutine,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(GoSleepVioletStart, GoSleepVioletEnd))),
                contentAlignment = Alignment.Center,
            ) {
                Text("Start Sleep Routine", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}
