package com.aca.people.domain.repository

import com.aca.people.domain.model.UserNote
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeAllNotes(): Flow<Map<String, UserNote>>
    fun observeNoteForUser(uuid: String): Flow<UserNote?>
    suspend fun getNoteForUser(uuid: String): UserNote?
    suspend fun saveNote(userUuid: String, content: String)
    suspend fun deleteNote(userUuid: String)
}
