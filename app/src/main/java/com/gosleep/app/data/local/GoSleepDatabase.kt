package com.gosleep.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gosleep.app.data.local.dao.BrainDumpDao
import com.gosleep.app.data.local.dao.SleepSessionDao
import com.gosleep.app.data.local.entity.BrainDumpEntity
import com.gosleep.app.data.local.entity.SleepSessionEntity

@Database(
    entities = [SleepSessionEntity::class, BrainDumpEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GoSleepDatabase : RoomDatabase() {

    abstract fun sleepSessionDao(): SleepSessionDao
    abstract fun brainDumpDao(): BrainDumpDao

    companion object {
        @Volatile
        private var INSTANCE: GoSleepDatabase? = null

        fun getInstance(context: Context): GoSleepDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    GoSleepDatabase::class.java,
                    "gosleep.db"
                ).build().also { INSTANCE = it }
            }
    }
}
