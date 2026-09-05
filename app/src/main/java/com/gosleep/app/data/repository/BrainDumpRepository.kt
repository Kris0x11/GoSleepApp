package com.gosleep.app.data.repository

import com.gosleep.app.data.local.dao.BrainDumpDao
import com.gosleep.app.data.local.entity.BrainDumpEntity
import kotlinx.coroutines.flow.Flow

class BrainDumpRepository(private val dao: BrainDumpDao) {

    fun observeUnresolvedNotes(): Flow<List<BrainDumpEntity>> = dao.observeUnresolved()

    suspend fun addTextNote(content: String) {
        if (content.isBlank()) return
        dao.insert(
            BrainDumpEntity(
                content = content.trim(),
                isVoiceNote = false,
                createdAtEpochMillis = System.currentTimeMillis(),
            )
        )
    }

    suspend fun addVoiceNote(transcript: String) {
        dao.insert(
            BrainDumpEntity(
                content = transcript,
                isVoiceNote = true,
                createdAtEpochMillis = System.currentTimeMillis(),
            )
        )
    }

    suspend fun delete(note: BrainDumpEntity) = dao.delete(note)

    suspend fun markResolved(note: BrainDumpEntity) = dao.markResolved(note.id)
}
