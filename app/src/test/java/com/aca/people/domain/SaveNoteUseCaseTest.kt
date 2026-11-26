package com.aca.people.domain

import com.aca.people.domain.repository.NoteRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

@ExtendWith(MockitoExtension::class)
class SaveNoteUseCaseTest {

    @Mock
    private lateinit var noteRepository: NoteRepository

    private lateinit var saveNoteUseCase: SaveNoteUseCase

    @BeforeEach
    fun setUp() {
        saveNoteUseCase = SaveNoteUseCase(noteRepository)
    }

    @Test
    fun `invoke trims content and saves note`() = runTest {
        saveNoteUseCase("uuid-1", "  Important client  ")
        verify(noteRepository).saveNote("uuid-1", "Important client")
    }

    @Test
    fun `invoke deletes note when content is blank`() = runTest {
        saveNoteUseCase("uuid-1", "   ")
        verify(noteRepository).deleteNote("uuid-1")
    }

    @Test
    fun `invoke ignores blank uuid`() = runTest {
        saveNoteUseCase("  ", "Note")
        verifyNoInteractions(noteRepository)
    }
}
