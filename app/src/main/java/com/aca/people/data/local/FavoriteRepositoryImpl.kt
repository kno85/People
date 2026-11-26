package com.aca.people.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aca.people.domain.User
import com.aca.people.domain.model.FavoriteUser
import com.aca.people.domain.repository.FavoriteRepository
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
class FavoriteRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : FavoriteRepository {

    private val favoritesMapType = object : TypeToken<LinkedHashMap<String, FavoriteUser>>() {}.type

    private val favoritesMapFlow: Flow<Map<String, FavoriteUser>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> decodeFavorites(preferences[FAVORITES_KEY]) }
        .distinctUntilChanged()

    override fun observeFavorites(): Flow<List<FavoriteUser>> =
        favoritesMapFlow.map { map ->
            map.values.sortedByDescending { it.addedAtEpochMillis }
        }

    override fun observeFavoriteUuids(): Flow<Set<String>> =
        favoritesMapFlow.map { it.keys }.distinctUntilChanged()

    override fun isFavorite(uuid: String): Flow<Boolean> {
        if (uuid.isBlank()) return flowOf(false)
        return observeFavoriteUuids().map { uuid in it }.distinctUntilChanged()
    }

    override suspend fun getFavoriteByUuid(uuid: String): FavoriteUser? {
        if (uuid.isBlank()) return null
        return favoritesMapFlow.first()[uuid]
    }

    override suspend fun addFavorite(user: User) {
        val uuid = user.login?.uuid?.takeIf { it.isNotBlank() } ?: return
        dataStore.edit { preferences ->
            val current = decodeFavorites(preferences[FAVORITES_KEY]).toMutableMap()
            if (!current.containsKey(uuid)) {
                current[uuid] = FavoriteUser(
                    uuid = uuid,
                    user = user,
                    addedAtEpochMillis = System.currentTimeMillis()
                )
                preferences[FAVORITES_KEY] = gson.toJson(current, favoritesMapType)
            }
        }
    }

    override suspend fun removeFavorite(uuid: String) {
        if (uuid.isBlank()) return
        dataStore.edit { preferences ->
            val current = decodeFavorites(preferences[FAVORITES_KEY]).toMutableMap()
            if (current.remove(uuid) != null) {
                preferences[FAVORITES_KEY] = gson.toJson(current, favoritesMapType)
            }
        }
    }

    override suspend fun toggleFavorite(user: User) {
        val uuid = user.login?.uuid?.takeIf { it.isNotBlank() } ?: return
        dataStore.edit { preferences ->
            val current = decodeFavorites(preferences[FAVORITES_KEY]).toMutableMap()
            if (current.containsKey(uuid)) {
                current.remove(uuid)
            } else {
                current[uuid] = FavoriteUser(
                    uuid = uuid,
                    user = user,
                    addedAtEpochMillis = System.currentTimeMillis()
                )
            }
            preferences[FAVORITES_KEY] = gson.toJson(current, favoritesMapType)
        }
    }

    private fun decodeFavorites(rawJson: String?): Map<String, FavoriteUser> {
        if (rawJson.isNullOrBlank()) return emptyMap()
        return try {
            gson.fromJson<Map<String, FavoriteUser>>(rawJson, favoritesMapType) ?: emptyMap()
        } catch (_: JsonSyntaxException) {
            emptyMap()
        }
    }

    private companion object {
        val FAVORITES_KEY = stringPreferencesKey("favorites_json")
    }
}
