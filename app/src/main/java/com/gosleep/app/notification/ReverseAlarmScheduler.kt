package com.gosleep.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

/**
 * Pianifica il trigger del Reverse Alarm: come da documento

 */
class ReverseAlarmScheduler(private val context: Context) {

    private val alarmManager: AlarmManager
        get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /** @param bedtimeMinutes minuti da mezzanotte, es. 22:30 -> 1350. */
    fun schedule(bedtimeMinutes: Int) {
        val triggerAtMillis = nextTriggerMillis(bedtimeMinutes)
        val pendingIntent = buildPendingIntent()

        if (canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    fun cancel() {
        alarmManager.cancel(buildPendingIntent())
    }

    private fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    private fun buildPendingIntent(): PendingIntent {
        val intent = Intent(context, ReverseAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /** Calcola il prossimo epoch millis per l'orario target odierno (o domani se già passato). */
    internal fun nextTriggerMillis(bedtimeMinutes: Int, now: Calendar = Calendar.getInstance()): Long {
        val target = now.clone() as Calendar
        target.set(Calendar.HOUR_OF_DAY, bedtimeMinutes / 60)
        target.set(Calendar.MINUTE, bedtimeMinutes % 60)
        target.set(Calendar.SECOND, 0)
        target.set(Calendar.MILLISECOND, 0)

        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }
}
