package com.aca.people.data

import com.aca.people.domain.User
import com.aca.people.presentation.UiResult

// Data/UserRepository.kt
interface UserRepository {
    suspend fun getUsers(page: Int, pageSize: Int): UiResult
}
