package com.aca.people.repository.paging

import com.aca.people.domain.User

// Data/UserRepository.kt
interface UserRepository {
    suspend fun getUsers(page: Int, pageSize: Int): List<User>
}
