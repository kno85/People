package com.aca.people.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aca.people.domain.model.UserNote
import com.aca.people.domain.repository.NoteRepository
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : NoteRepository {

    private val notesMapType = object : TypeToken<LinkedHashMap<String, UserNote>>() {}.type

    private val notesMapFlow: Flow<Map<String, UserNote>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> decodeNotes(preferences[NOTES_KEY]) }
        .distinctUntilChanged()

    override fun observeAllNotes(): Flow<Map<String, UserNote>> = notesMapFlow

    override fun observeNoteForUser(uuid: String): Flow<UserNote?> {
        if (uuid.isBlank()) return flowOf(null)
        return notesMapFlow.map { it[uuid] }.distinctUntilChanged()
    }

    override suspend fun getNoteForUser(uuid: String): UserNote? {
        if (uuid.isBlank()) return null
        return notesMapFlow.first()[uuid]
    }

    override suspend fun saveNote(userUuid: String, content: String) {
        if (userUuid.isBlank()) return
        val trimmedContent = content.trim()
        if (trimmedContent.isEmpty()) {
            deleteNote(userUuid)
            return
        }
        dataStore.edit { preferences ->
            val current = decodeNotes(preferences[NOTES_KEY]).toMutableMap()
            current[userUuid] = UserNote(
                userUuid = userUuid,
                content = trimmedContent,
                updatedAtEpochMillis = System.currentTimeMillis()
            )
            preferences[NOTES_KEY] = gson.toJson(current, notesMapType)
        }
    }

    override suspend fun deleteNote(userUuid: String) {
        if (userUuid.isBlank()) return
        dataStore.edit { preferences ->
            val current = decodeNotes(preferences[NOTES_KEY]).toMutableMap()
            if (current.remove(userUuid) != null) {
                preferences[NOTES_KEY] = gson.toJson(current, notesMapType)
            }
        }
    }

    private fun decodeNotes(rawJson: String?): Map<String, UserNote> {
        if (rawJson.isNullOrBlank()) return emptyMap()
        return try {
            gson.fromJson<Map<String, UserNote>>(rawJson, notesMapType) ?: emptyMap()
        } catch (_: JsonSyntaxException) {
            emptyMap()
        }
    }

    private companion object {
        val NOTES_KEY = stringPreferencesKey("notes_json")
    }
}
