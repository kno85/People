package com.aca.people.repository

import androidx.paging.PagingData
import com.aca.people.domain.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUsers(): Flow<PagingData<User>>
    suspend fun getUserByUuid(uuid: String): User?
}
