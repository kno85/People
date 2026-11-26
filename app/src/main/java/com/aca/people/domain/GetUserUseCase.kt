package com.aca.people.domain

import com.aca.people.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uuid: String): User? {
        return userRepository.getUserByUuid(uuid)
    }
}