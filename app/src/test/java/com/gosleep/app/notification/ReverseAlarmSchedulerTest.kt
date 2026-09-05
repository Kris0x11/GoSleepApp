package com.gosleep.app.notification

import android.content.Context
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class ReverseAlarmSchedulerTest {

    private val context = mockk<Context>(relaxed = true)
    private val scheduler = ReverseAlarmScheduler(context)

    private fun calendarAt(hour: Int, minute: Int): Calendar =
        Calendar.getInstance().apply {
            set(2026, Calendar.JULY, 11, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }

    @Test
    fun `schedules for today when bedtime is still ahead`() {
        val now = calendarAt(20, 0) // 20:00
        val bedtimeMinutes = 22 * 60 + 30 // 22:30

        val triggerMillis = scheduler.nextTriggerMillis(bedtimeMinutes, now)

        val expected = calendarAt(22, 30)
        assertEquals(expected.timeInMillis, triggerMillis)
    }

    @Test
    fun `schedules for tomorrow when bedtime has already passed today`() {
        val now = calendarAt(23, 0) // 23:00
        val bedtimeMinutes = 22 * 60 + 30 // 22:30, already passed

        val triggerMillis = scheduler.nextTriggerMillis(bedtimeMinutes, now)

        val expected = calendarAt(22, 30).apply { add(Calendar.DAY_OF_YEAR, 1) }
        assertEquals(expected.timeInMillis, triggerMillis)
    }

    @Test
    fun `schedules immediately-following minute when now equals bedtime`() {
        val now = calendarAt(22, 30)
        val bedtimeMinutes = 22 * 60 + 30

        val triggerMillis = scheduler.nextTriggerMillis(bedtimeMinutes, now)

        // now == target -> considerato "già passato", si sposta al giorno dopo
        val expected = calendarAt(22, 30).apply { add(Calendar.DAY_OF_YEAR, 1) }
        assertEquals(expected.timeInMillis, triggerMillis)
    }
}
