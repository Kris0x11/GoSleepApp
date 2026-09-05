package com.gosleep.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gosleep.app.data.local.entity.BrainDumpEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrainDumpDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: BrainDumpEntity): Long

    @Delete
    suspend fun delete(note: BrainDumpEntity)

    @Query("SELECT * FROM brain_dump_notes WHERE resolved = 0 ORDER BY createdAtEpochMillis DESC")
    fun observeUnresolved(): Flow<List<BrainDumpEntity>>

    @Query("UPDATE brain_dump_notes SET resolved = 1 WHERE id = :id")
    suspend fun markResolved(id: Long)
}
