package com.gosleep.app

import android.app.Application
import com.gosleep.app.data.datastore.UserPreferencesRepository
import com.gosleep.app.data.local.GoSleepDatabase
import com.gosleep.app.data.repository.BrainDumpRepository
import com.gosleep.app.data.repository.SleepRepository
import com.gosleep.app.notification.ReverseAlarmScheduler



class GoSleepApplication : Application() {

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set
    lateinit var sleepRepository: SleepRepository
        private set
    lateinit var brainDumpRepository: BrainDumpRepository
        private set
    lateinit var reverseAlarmScheduler: ReverseAlarmScheduler
        private set

    override fun onCreate() {
        super.onCreate()
        val database = GoSleepDatabase.getInstance(this)
        userPreferencesRepository = UserPreferencesRepository(this)
        reverseAlarmScheduler = ReverseAlarmScheduler(this)
        sleepRepository = SleepRepository(
            database.sleepSessionDao(),
            userPreferencesRepository,
            reverseAlarmScheduler,
        )
        brainDumpRepository = BrainDumpRepository(database.brainDumpDao())
    }
}
