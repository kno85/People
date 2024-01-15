package com.aca.people.data

import com.aca.people.domain.User
import com.aca.people.mapToDomain
import com.aca.people.network.ApiService

// Data/UserRepositoryImpl.kt
class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {
    override suspend fun getUsers(page: Int, pageSize: Int): List<User?> {
        val response = apiService.getUsers(ApiService.key, page, pageSize)
        return mapToDomain(response.results)
    }

}
