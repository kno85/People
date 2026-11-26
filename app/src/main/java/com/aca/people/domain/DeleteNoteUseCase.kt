package com.aca.people.domain

import com.aca.people.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(userUuid: String) {
        if (userUuid.isNotBlank()) {
            noteRepository.deleteNote(userUuid)
        }
    }
}
