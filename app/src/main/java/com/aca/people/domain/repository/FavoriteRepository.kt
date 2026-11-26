package com.aca.people.domain.repository

import com.aca.people.domain.User
import com.aca.people.domain.model.FavoriteUser
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<FavoriteUser>>
    fun observeFavoriteUuids(): Flow<Set<String>>
    fun isFavorite(uuid: String): Flow<Boolean>
    suspend fun getFavoriteByUuid(uuid: String): FavoriteUser?
    suspend fun addFavorite(user: User)
    suspend fun removeFavorite(uuid: String)
    suspend fun toggleFavorite(user: User)
}
