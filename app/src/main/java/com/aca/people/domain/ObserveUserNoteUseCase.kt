package com.aca.people.domain

import com.aca.people.domain.model.UserNote
import com.aca.people.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(uuid: String): Flow<UserNote?> = noteRepository.observeNoteForUser(uuid)
}
