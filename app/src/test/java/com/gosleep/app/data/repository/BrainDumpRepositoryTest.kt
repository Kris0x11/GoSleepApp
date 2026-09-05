package com.gosleep.app.data.repository

import com.gosleep.app.data.local.dao.BrainDumpDao
import com.gosleep.app.data.local.entity.BrainDumpEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Fake DAO in-memory: evita di dipendere da Room/Robolectric per testare
 * la logica del repository in isolamento.
 */
private class FakeBrainDumpDao : BrainDumpDao {
    private val notes = MutableStateFlow<List<BrainDumpEntity>>(emptyList())
    private var nextId = 1L

    override suspend fun insert(note: BrainDumpEntity): Long {
        val withId = note.copy(id = nextId++)
        notes.value = notes.value + withId
        return withId.id
    }

    override suspend fun delete(note: BrainDumpEntity) {
        notes.value = notes.value.filterNot { it.id == note.id }
    }

    override fun observeUnresolved(): Flow<List<BrainDumpEntity>> = notes.asStateFlow()

    override suspend fun markResolved(id: Long) {
        notes.value = notes.value.map { if (it.id == id) it.copy(resolved = true) else it }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class BrainDumpRepositoryTest {

    @Test
    fun `adding a text note stores trimmed content`() = runTest {
        val dao = FakeBrainDumpDao()
        val repository = BrainDumpRepository(dao)

        repository.addTextNote("  worried about the deadline  ")

        val notes = repository.observeUnresolvedNotes().first()
        assertEquals("worried about the deadline", notes.first().content)
    }

    @Test
    fun `blank text note is ignored`() = runTest {
        val dao = FakeBrainDumpDao()
        val repository = BrainDumpRepository(dao)

        repository.addTextNote("   ")

        assertTrue(repository.observeUnresolvedNotes().first().isEmpty())
    }

    @Test
    fun `voice note is flagged accordingly`() = runTest {
        val dao = FakeBrainDumpDao()
        val repository = BrainDumpRepository(dao)

        repository.addVoiceNote("call the accountant tomorrow")

        val note = repository.observeUnresolvedNotes().first().first()
        assertTrue(note.isVoiceNote)
    }

    @Test
    fun `deleting a note removes it from the unresolved list`() = runTest {
        val dao = FakeBrainDumpDao()
        val repository = BrainDumpRepository(dao)
        repository.addTextNote("pay invoice")
        val note = repository.observeUnresolvedNotes().first().first()

        repository.delete(note)

        assertTrue(repository.observeUnresolvedNotes().first().isEmpty())
    }
}
