package com.aca.people.domain

import androidx.paging.PagingData
import com.aca.people.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<PagingData<User>> {
        return userRepository.getUsers()
    }
}

