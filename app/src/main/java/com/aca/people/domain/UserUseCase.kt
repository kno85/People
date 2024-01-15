package com.aca.people.domain

import com.aca.people.data.UserRepository
import com.aca.people.presentation.UiResult

// Domain/UserUseCase.kt
class UserUseCase(private val userRepository: UserRepository) {
    suspend fun getUsers(page: Int, pageSize: Int): UiResult {
        return userRepository.getUsers(page, pageSize)
    }
}
