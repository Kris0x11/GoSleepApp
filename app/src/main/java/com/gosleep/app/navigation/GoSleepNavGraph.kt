package com.gosleep.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gosleep.app.GoSleepApplication
import com.gosleep.app.ui.braindump.BrainDumpScreen
import com.gosleep.app.ui.braindump.BrainDumpViewModel
import com.gosleep.app.ui.dashboard.DashboardScreen
import com.gosleep.app.ui.dashboard.DashboardViewModel
import com.gosleep.app.ui.distraction.DistractionBlockScreen
import com.gosleep.app.ui.distraction.DistractionBlockViewModel
import com.gosleep.app.ui.morning.MorningFeedbackScreen
import com.gosleep.app.ui.morning.MorningFeedbackViewModel
import com.gosleep.app.ui.nightmode.NightModeIntroScreen
import com.gosleep.app.ui.onboarding.OnboardingScreen
import com.gosleep.app.ui.onboarding.OnboardingViewModel
import com.gosleep.app.ui.prep.SleepPreparationScreen
import com.gosleep.app.ui.prep.SleepPreparationViewModel
import com.gosleep.app.ui.relax.RelaxModeScreen
import com.gosleep.app.ui.relax.RelaxModeViewModel
import com.gosleep.app.ui.routine.RoutineFlowScreen
import com.gosleep.app.ui.routine.RoutineFlowViewModel

/**
 * Implementata la Site-Map:
 * ONBOARDING -> DASHBOARD -> { SLEEP_PREPARATION, BRAIN_DUMP, NIGHT_MODE_INTRO -> ROUTINE_FLOW -> RELAX_MODE }
 * ONLY WHEN TRIGGERED -> DISTRACTION_BLOCK, MORNING_FEEDBACK
 */
@Composable
fun GoSleepNavGraph(
    app: GoSleepApplication,
    startDestination: String = Routes.ONBOARDING,
    navController: NavHostController = rememberNavController(),
) {
    val factory = AppViewModelFactory(app)
    var usedRelaxOrBrainDumpThisNight = false

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.ONBOARDING) {
            val viewModel: OnboardingViewModel = viewModel(factory = factory)
            OnboardingScreen(
                viewModel = viewModel,
                onOnboardingComplete = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.DASHBOARD) {
            val viewModel: DashboardViewModel = viewModel(factory = factory)
            DashboardScreen(
                viewModel = viewModel,
                onOpenBrainDump = { navController.navigate(Routes.BRAIN_DUMP) },
                onOpenNightMode = { navController.navigate(Routes.NIGHT_MODE_INTRO) },
                onOpenSleepPreparation = { navController.navigate(Routes.SLEEP_PREPARATION) },
            )
        }

        composable(Routes.SLEEP_PREPARATION) {
            val viewModel: SleepPreparationViewModel = viewModel(factory = factory)
            SleepPreparationScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.NIGHT_MODE_INTRO) {
            NightModeIntroScreen(
                onStartSleepRoutine = { navController.navigate(Routes.ROUTINE_FLOW) },
            )
        }

        composable(Routes.BRAIN_DUMP) {
            val viewModel: BrainDumpViewModel = viewModel(factory = factory)
            BrainDumpScreen(
                viewModel = viewModel,
                onFinished = {
                    usedRelaxOrBrainDumpThisNight = true
                    navController.popBackStack()
                },
                onOpenRelaxMode = {
                    usedRelaxOrBrainDumpThisNight = true
                    navController.navigate(Routes.RELAX_MODE)
                },
            )
        }

        composable(Routes.ROUTINE_FLOW) {
            val viewModel: RoutineFlowViewModel = viewModel(factory = factory)
            RoutineFlowScreen(
                viewModel = viewModel,
                onRoutineCompleted = { navController.navigate(Routes.RELAX_MODE) },
                onRoutineSkipped = { navController.popBackStack() },
            )
        }

        composable(Routes.RELAX_MODE) {
            val viewModel: RelaxModeViewModel = viewModel(factory = factory)
            RelaxModeScreen(
                viewModel = viewModel,
                onGoingToSleepNow = {
                    usedRelaxOrBrainDumpThisNight = true
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.DISTRACTION_BLOCK) {
            val viewModel: DistractionBlockViewModel = viewModel(factory = factory)
            val bedtime by app.sleepRepository.bedtimeMinutes.collectAsState(initial = 0)
            DistractionBlockScreen(
                viewModel = viewModel,
                bedtimeTargetMinutes = bedtime,
                onSleepModeActivated = { navController.navigate(Routes.ROUTINE_FLOW) },
                onStayedAwakeAcknowledged = { navController.popBackStack() },
            )
        }

        composable(Routes.MORNING_FEEDBACK) {
            val viewModel: MorningFeedbackViewModel = viewModel(factory = factory)
            MorningFeedbackScreen(
                viewModel = viewModel,
                usedRelaxOrBrainDump = usedRelaxOrBrainDumpThisNight,
                onDone = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
            )
        }
    }
}
