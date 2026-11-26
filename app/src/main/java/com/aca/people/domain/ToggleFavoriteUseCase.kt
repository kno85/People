package com.aca.people.domain

import com.aca.people.domain.repository.FavoriteRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(user: User) {
        if (!user.login?.uuid.isNullOrBlank()) {
            favoriteRepository.toggleFavorite(user)
        }
    }
}
