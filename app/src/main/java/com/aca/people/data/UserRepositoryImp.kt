package com.aca.people.data

import com.aca.people.mapToDomain
import com.aca.people.network.ApiService
import com.aca.people.presentation.UiResult

// Data/UserRepositoryImpl.kt
class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {
    override suspend fun getUsers(page: Int, pageSize: Int): UiResult {
        val response = apiService.getUsers(ApiService.key, page, pageSize)
        return UiResult(mapToDomain(response.results),"")
    }

}
