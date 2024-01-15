package com.aca.people.domain

import com.aca.people.data.UserRepository

// Domain/UserUseCase.kt
class UserUseCase(private val userRepository: UserRepository) {
    suspend fun getUsers(page: Int, pageSize: Int): List<User?> {
        return userRepository.getUsers(page, pageSize)
    }
}
