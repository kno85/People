package com.aca.people.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class NoteRepositoryImplTest {

    @TempDir
    lateinit var tempDir: File

    private fun createRepository(scope: TestScope, fileName: String): NoteRepositoryImpl {
        val dataStore = PreferenceDataStoreFactory.create(
            scope = scope.backgroundScope,
            produceFile = { File(tempDir, "$fileName.preferences_pb") }
        )
        return NoteRepositoryImpl(dataStore, Gson())
    }

    @Test
    fun `saveNote creates and edits note for user`() = runTest(UnconfinedTestDispatcher()) {
        val repository = createRepository(this, "create_edit")
        val uuid = "user-1"

        repository.saveNote(uuid, "  Met at conference  ")
        val created = repository.getNoteForUser(uuid)
        assertEquals("Met at conference", created?.content)
        assertEquals(uuid, created?.userUuid)

        repository.saveNote(uuid, "Updated note")
        val updated = repository.observeNoteForUser(uuid).first()
        assertEquals("Updated note", updated?.content)
        assertEquals(1, repository.observeAllNotes().first().size)
    }

    @Test
    fun `deleteNote removes existing note`() = runTest(UnconfinedTestDispatcher()) {
        val repository = createRepository(this, "delete")
        val uuid = "user-2"

        repository.saveNote(uuid, "Temporary note")
        assertEquals("Temporary note", repository.getNoteForUser(uuid)?.content)

        repository.deleteNote(uuid)

        assertNull(repository.getNoteForUser(uuid))
        assertNull(repository.observeNoteForUser(uuid).first())
        assertTrue(repository.observeAllNotes().first().isEmpty())
    }

    @Test
    fun `getNoteForUser returns null when user has no note`() = runTest(UnconfinedTestDispatcher()) {
        val repository = createRepository(this, "missing")

        assertNull(repository.getNoteForUser("unknown-uuid"))
        assertNull(repository.observeNoteForUser("unknown-uuid").first())
    }

    @Test
    fun `saveNote with blank content deletes existing note`() = runTest(UnconfinedTestDispatcher()) {
        val repository = createRepository(this, "blank_deletes")
        val uuid = "user-3"

        repository.saveNote(uuid, "Initial")
        repository.saveNote(uuid, "   ")

        assertNull(repository.getNoteForUser(uuid))
    }
}
