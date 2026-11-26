package com.aca.people.domain

import com.aca.people.domain.model.FavoriteUser
import com.aca.people.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<FavoriteUser>> = favoriteRepository.observeFavorites()
}
