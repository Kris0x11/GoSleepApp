package com.gosleep.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gosleep.app.ui.theme.*

private const val STEP_WELCOME = 0

/**
 * Onboarding fedele al mock: schermata di Benvenuto -> 4 domande con barra di
 * avanzamento "Question X/4" + percentuale -> completamento diretto in dashboard.
 */
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit,
) {
    var step by remember { mutableIntStateOf(STEP_WELCOME) }
    val answers = remember { mutableStateListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NightBackground)
            .padding(24.dp),
    ) {
        if (step == STEP_WELCOME) {
            WelcomePage(onGetStarted = { step = 1 })
        } else {
            val questionIndex = step - 1
            val question = ONBOARDING_QUESTIONS[questionIndex]
            QuestionPage(
                questionNumber = questionIndex + 1,
                totalQuestions = ONBOARDING_QUESTIONS.size,
                question = question,
                onAnswerSelected = { answer ->
                    answers.add(answer)
                    if (questionIndex + 1 < ONBOARDING_QUESTIONS.size) {
                        step += 1
                    } else {
                        viewModel.completeOnboarding(answers.toList(), onOnboardingComplete)
                    }
                },
            )
        }
    }
}

@Composable
private fun WelcomePage(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(GoSleepVioletStart.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.NightsStay,
                contentDescription = null,
                tint = GoSleepVioletStart,
                modifier = Modifier.size(40.dp),
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Welcome to GoSleep",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Let's understand your sleep patterns to create a personalized bedtime experience",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
            contentPadding = PaddingValues(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(GoSleepVioletStart, GoSleepVioletEnd))),
                contentAlignment = Alignment.Center,
            ) {
                Text("Get Started", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun QuestionPage(
    questionNumber: Int,
    totalQuestions: Int,
    question: OnboardingQuestion,
    onAnswerSelected: (String) -> Unit,
) {
    val progress = questionNumber / totalQuestions.toFloat()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Question $questionNumber/$totalQuestions", style = MaterialTheme.typography.bodyMedium)
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = GoSleepVioletStart,
            trackColor = CardSurfaceElevated,
        )

        Spacer(Modifier.height(64.dp))
        Text(
            question.question,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(32.dp))

        question.options.forEach { option ->
            Surface(
                onClick = { onAnswerSelected(option) },
                color = CardSurface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    option,
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
