package com.aca.people.domain

import com.aca.people.domain.repository.NoteRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(userUuid: String, content: String) {
        if (userUuid.isBlank()) return
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {
            noteRepository.deleteNote(userUuid)
        } else {
            noteRepository.saveNote(userUuid, trimmed)
        }
    }
}
