package com.gosleep.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gosleep.app.data.local.entity.SleepSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SleepSessionEntity): Long

    @Update
    suspend fun update(session: SleepSessionEntity)

    @Query("SELECT * FROM sleep_sessions WHERE dateEpochDay = :epochDay LIMIT 1")
    suspend fun getByDate(epochDay: Long): SleepSessionEntity?

    @Query("SELECT * FROM sleep_sessions ORDER BY dateEpochDay DESC LIMIT 1")
    fun observeLatest(): Flow<SleepSessionEntity?>

    @Query("SELECT * FROM sleep_sessions ORDER BY dateEpochDay DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<SleepSessionEntity>>

    @Query("SELECT * FROM sleep_sessions ORDER BY dateEpochDay DESC")
    suspend fun getAllOrdered(): List<SleepSessionEntity>
}
