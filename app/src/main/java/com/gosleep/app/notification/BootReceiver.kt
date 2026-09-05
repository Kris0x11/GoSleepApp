package com.gosleep.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gosleep.app.GoSleepApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val app = context.applicationContext as GoSleepApplication

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val minutes = app.userPreferencesRepository.bedtimeMinutes.first()
                app.reverseAlarmScheduler.schedule(minutes)
            } finally {
                pendingResult.finish()
            }
        }
    }
}