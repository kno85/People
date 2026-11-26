package com.aca.people.domain

import com.aca.people.domain.repository.FavoriteRepository
import com.aca.people.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(uuid: String): User? {
        return userRepository.getUserByUuid(uuid)
            ?: favoriteRepository.getFavoriteByUuid(uuid)?.user
    }
}

