package com.gosleep.app.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.gosleep.app.GoSleepApplication
import com.gosleep.app.navigation.AppViewModelFactory
import com.gosleep.app.ui.distraction.DistractionBlockScreen
import com.gosleep.app.ui.distraction.DistractionBlockViewModel
import com.gosleep.app.ui.theme.GoSleepTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class DistractionBlockActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        val app = application as GoSleepApplication
        val bedtimeMinutes = runBlocking { app.sleepRepository.bedtimeMinutes.first() }

        setContent {
            GoSleepTheme {
                val viewModel: DistractionBlockViewModel = viewModel(factory = AppViewModelFactory(app))
                DistractionBlockScreen(
                    viewModel = viewModel,
                    bedtimeTargetMinutes = bedtimeMinutes,
                    onSleepModeActivated = {
                        activateDoNotDisturbAndLock()
                        finish()
                    },
                    onStayedAwakeAcknowledged = {
                        finish()
                    },
                )
            }
        }
    }

    /**
     * "Al click, il telefono blocca lo schermo e attiva la modalità non disturbare" —
     * riduzione del carico: un solo tap avvia automaticamente il resto.
     */
    private fun activateDoNotDisturbAndLock() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        }

        // (DevicePolicyManager.lockNow()) concessi esplicitamente dall'utente nelle
        // impostazioni di sistema; qui l'attività si limita a chiudersi, lasciando
        // che il sistema torni alla lockscreen naturalmente.
    }
}
